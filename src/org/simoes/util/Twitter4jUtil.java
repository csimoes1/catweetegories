package org.simoes.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import org.simoes.classify.Category;
import org.simoes.common.StatusPlus;
import org.simoes.servlet.dao.TwitterUser;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class Twitter4jUtil {
	static Logger log = Logger.getLogger(Twitter4jUtil.class.getName());
	private static TwitterFactory factory = new TwitterFactory();
	private static int apiCalls = 0;
	public static Twitter getTwitterInstance() {
		return factory.getInstance();
	}

	public static List<Status> loadTweetsByUser(Twitter twitter, TwitterUser twitterUser, int numberOfPages) throws TwitterException {
    	List<Status> result = new ArrayList<Status>();
		Paging paging = new Paging(1, Constants.TWITTER_PAGE_SIZE);
		for (int i = 1; i <= numberOfPages; i++) {
			paging.setPage(i);
			logAPICall();
			ResponseList<Status> statuses = twitter.getUserTimeline(twitterUser.getId(), paging);
			if(statuses.size() <= 0) {
				log.info("End of timeline for user: " + twitterUser);
				break;
			}
			result.addAll(statuses);
		}
    	
    	return result;
    }

	/**
	 * We have to return StatusPlus objects because every call to twitter.getUserTimeline() blows away our
	 * DataObjectFactory that we need to get the raw JSON.  This is why we have to pass the Category into this method.
	 * 
	 * @param twitter
	 * @param screenName
	 * @param numberOfPages
	 * @return
	 * @throws TwitterException
	 */
	public static void storeTweetsByUser(Twitter twitter, String screenName, int numberOfPages, Category category) 
			throws TwitterException 
	{
		log.info("storeTweetsByUser called for: " + screenName);
		Paging paging = new Paging(1, Constants.TWITTER_PAGE_SIZE);
		for (int i = 1; i <= numberOfPages; i++) {
	    	List<StatusPlus> statusPluses = new ArrayList<StatusPlus>();
			paging.setPage(i);
			logAPICall();
			ResponseList<Status> statuses = twitter.getUserTimeline(screenName, paging);
			if(statuses.size() <= 0) {
				log.info("End of timeline for user: " + screenName);
				break;
			}
			for (Status status : statuses) {
				// create a StatusPlus and add it to the result List
				StatusPlus sp = new StatusPlus();
				sp.setStatus(status);
				sp.setCategory(category);
				sp.setUserId(status.getUser().getId());
				sp.setUserCategorized(false); //these are machine classified
				statusPluses.add(sp);
			}
			MongoDbUtil.getInstance().insertStatuses(MongoDbUtil.COLL_COLLECT, statusPluses);			
			
		}
    }
	
    /**
     * Loads Tweets for the User and stores them in the database
     * @param twitter
     * @return
     * @throws TwitterException
     */
    public static List<Status> loadHomePageTweets(Twitter twitter, int numberOfPages) throws TwitterException {
    	List<Status> result = new ArrayList<Status>();
		Paging paging = new Paging(1, Constants.TWITTER_PAGE_SIZE);
		for (int i = 1; i <= numberOfPages; i++) {
			paging.setPage(i);
			logAPICall();
			ResponseList<Status> statuses = twitter.getHomeTimeline(paging);
			if(statuses.size() <= 0) {
				log.info("End of timeline for user: " + twitter.getScreenName());
				break;
			}
			result.addAll(statuses);
		}

		return result;
    }

    private static void logAPICall() {
    	//Date d = Calendar.getInstance().getTime();
		log.info("About to make Twitter API call " + apiCalls);
		apiCalls++;
    }

}
