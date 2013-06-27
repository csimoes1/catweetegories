package org.simoes.action;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.struts2.interceptor.SessionAware;
import org.simoes.classify.Category;
import org.simoes.classify.Classifier;
import org.simoes.classify.ClassifierStats;
import org.simoes.classify.TweetClassifier;
import org.simoes.common.StatusPlus;
import org.simoes.servlet.dao.TwitterUser;
import org.simoes.util.Constants;
import org.simoes.util.MongoDbUtil;

import twitter4j.Twitter;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Handles returning the statistics on why a tweet was classified the way it was
 */
public class ClassifierStatsAction extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = -5737680819217552582L;

	static Logger log = Logger.getLogger(ClassifierStatsAction.class.getName());
	
	private Map<String, Object> userSession;
	private String statusId;
	private StatusPlus statusPlus;
	private TreeMap<Category, Double> categoryScores;
	private String scoringTable;
	
    public String execute() throws Exception {
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
    	
    	// load status
    	statusPlus = MongoDbUtil.getInstance().loadStatusPlus(statusId);
    	if(null != statusPlus) {
    		TweetClassifier tc = Classifier.getInstance();
    		ClassifierStats cs = tc.getStatistics(statusPlus.getStatus().getText());
    		categoryScores = cs.getCategoryScores();
    		scoringTable = cs.getScoringTable();
    	}
        return SUCCESS;
    }
    
    public void setSession(Map<String, Object> session) {
        userSession = session ;
    }

    
    public TwitterUser getTwitterUser() {
    	return (TwitterUser) userSession.get(Constants.SESSION_TWITTER_USER);
    }

    public Category[] getCategories() {
    	return Category.getAllCategories();
    }

	public String getStatusId() {
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

    public TreeMap<Category, Double> getCategoryScores() {
    	return categoryScores;
    }
    
    public String getScoringTable() {
    	return scoringTable;
    }
    
    public StatusPlus getStatusPlus() {
    	return statusPlus;
    }
}
