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
 * Loads up 100 tweets not graded and records them being scored for accuracy
 */
public class GradeAction extends ActionSupport implements SessionAware {
	static Logger log = Logger.getLogger(GradeAction.class.getName());
	public static int TWEETS_GRADED = 100;
	
	private Map<String, Object> userSession;
	private List<StatusPlus> statuses;
	
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
    	
    	// load up 100 most recent tweets that have NOT been graded
		statuses = getClassifiedStatuses(twitter, twitterUser);
    	
//    	if(ConfigResources.isTweetsCachedInSession()) {
//    		tempStatusList = (List<StatusPlus>) userSession.get(Constants.SESSION_STATUSES);
//    		if(null == tempStatusList) {
//    			tempStatusList = getClassifiedStatuses(twitter, twitterUser);
//    			// save to session
//    			userSession.put(Constants.SESSION_STATUSES, tempStatusList);
//    		}
//    	} else {
//    		tempStatusList = getClassifiedStatuses(twitter, twitterUser);
//    	}
    	
    	// if category is valid filter statuses by Category
//    	if(!Strings.isNullOrEmpty(category)) {
//        	Category chosenCat = Category.getCategoryByName(category);
//    		for (StatusPlus sp : tempStatusList) {
//				if(sp.getCategory().equals(chosenCat)) {
//					statuses.add(sp);
//				}
//			}
//    	} else { // else use all of the Statuses
//    		statuses = tempStatusList;
//    	}
        
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
    			MongoDbUtil.getInstance().loadAllStatusPlusesForUser(twitterUser, MongoDbUtil.COLL_STATUS, TWEETS_GRADED); 
//    	List<StatusPlus> addToDatabase = new ArrayList<StatusPlus>(); // tweets we need to add to MongoDB
//		TweetClassifier tc = Classifier.getInstance();
    	
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

}
