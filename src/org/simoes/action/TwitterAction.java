package org.simoes.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.logging.Logger;

import org.apache.struts2.interceptor.SessionAware;
import org.simoes.classify.Category;
import org.simoes.classify.Classifier;
import org.simoes.classify.TweetClassifier;
import org.simoes.common.StatusPlus;
import org.simoes.servlet.dao.TwitterUser;
import org.simoes.util.ConfigResources;
import org.simoes.util.Constants;
import org.simoes.util.MongoDbUtil;
import org.simoes.util.Twitter4jUtil;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.google.common.base.Strings;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <code>Set welcome message.</code>
 */
public class TwitterAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = -7499829674120825667L;
	static Logger log = Logger.getLogger(TwitterAction.class.getName());
	public static int TWITTER_PAGE_SIZE = 200;
	public static int TWITTER_DB_LOAD_SIZE = 1000;
	
	private Map<String, Object> userSession;
	private List<StatusPlus> statuses;
	private String category;
	
    public String execute() throws Exception {
    	List<StatusPlus> tempStatusList = new ArrayList<StatusPlus>();
    	statuses = new ArrayList<StatusPlus>();
    	
        Twitter twitter = (Twitter) userSession.get(Constants.SESSION_TWITTER_OBJ);
    	if(null == twitter) {
    		log.warning("twitter was not found in the Session");
    		return "login";
    	}
        TwitterUser twitterUser = getTwitterUser();
    	if(null == twitterUser) {
    		log.warning("twitterUser was not found in the Session");
    		return "login";
    	}
    	
    	// if tweets are cached in session try to get them from there and save them there
    	// this stops us from calling Twitter again and again during development
    	if(ConfigResources.isTweetsCachedInSession()) {
    		// if tweets not in session load them once and store them in the session, 
    		// note this is wasteful for memory and just for development
    		tempStatusList = (List<StatusPlus>) userSession.get(Constants.SESSION_STATUSES);
    		if(null == tempStatusList) {
    			tempStatusList = getClassifiedStatuses(twitter, twitterUser);
    			// save to session
    			userSession.put(Constants.SESSION_STATUSES, tempStatusList);
    		}
    	} else {
    		tempStatusList = getClassifiedStatuses(twitter, twitterUser);
    	}
    	
    	// if category is valid filter statuses by Category
    	if(!Strings.isNullOrEmpty(category)) {
        	Category chosenCat = Category.getCategoryByName(category);
    		for (StatusPlus sp : tempStatusList) {
				if(sp.getCategory().equals(chosenCat)) {
					statuses.add(sp);
				}
			}
    	} else { // else use all of the Statuses
    		statuses = tempStatusList;
    	}
        
        return SUCCESS;
    }
    
    public void setSession(Map<String, Object> session) {
        userSession = session ;
    }

    /**
     * Loads last 1000 tweets from database if we have any, and then combines them with the last 200
     * from Twitter.  All Tweets fresh from Twitter are classified using our algorithm
     * 
     * @param twitter
     * @return
     */
    private List<StatusPlus> getClassifiedStatuses(Twitter twitter, TwitterUser twitterUser) throws TwitterException {
    	// tweets already in MongoDB
    	SortedSet<StatusPlus> fromDatabase = 
    			MongoDbUtil.getInstance().loadAllStatusPluses(MongoDbUtil.COLL_STATUS, TWITTER_DB_LOAD_SIZE); 
    	List<StatusPlus> addToDatabase = new ArrayList<StatusPlus>(); // tweets we need to add to MongoDB
		TweetClassifier tc = Classifier.getInstance();
    	
		// loop over latest tweets and add to fromDatabase if we don't already have them
		// also add to addToDatabase so we can update our database
    	List<Status> baseStatuses = Twitter4jUtil.loadHomePageTweets(twitter, 1);
    	for (Status status : baseStatuses) {
			// classify tweet and add to result
    		StatusPlus sp = new StatusPlus();
    		sp.setStatus(status);
    		sp.setCategory(tc.classifyText(status.getText()));
    		sp.setUserId(twitterUser.getId());
    		sp.setUserCategorized(false); //these are machine classified
    		
    		// check to see if result already has this Tweet
    		if(!fromDatabase.contains(sp)) {
    			fromDatabase.add(sp);
    			addToDatabase.add(sp);
    		}
		}
    	// store these in the DB
    	MongoDbUtil.getInstance().insertStatuses(MongoDbUtil.COLL_STATUS, addToDatabase);
    	return new ArrayList<StatusPlus>(fromDatabase);
    }
    
    
    public TwitterUser getTwitterUser() {
    	return (TwitterUser) userSession.get(Constants.SESSION_TWITTER_USER);
    }

	public List<StatusPlus> getStatuses() {
		return statuses;
	}

    public Category[] getCategories() {
    	return Category.getAllCategories();
    }

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}
