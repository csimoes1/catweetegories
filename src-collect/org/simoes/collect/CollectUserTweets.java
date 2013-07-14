package org.simoes.collect;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.simoes.classify.Category;
import org.simoes.common.StatusPlus;
import org.simoes.util.ConfigResources;
import org.simoes.util.MongoDbUtil;
import org.simoes.util.Twitter4jUtil;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import au.com.bytecode.opencsv.CSVReader;

/**
 * This class is designed to collect tweets from various usernames per category
 * These are specified in the category.properties file
 * @author csimoes
 *
 */
public class CollectUserTweets implements Runnable {
	static Logger log = Logger.getLogger(CollectUserTweets.class.getName());

	public static final String DB = "collect";
	public static final String COLL = "categorized";
	public static final int MINS_15 = 900000; //this many ms in 15 minutes
	private String skipToUserName; //if this is set we will start collecting Tweets at this screenName
	
	private TreeMap<String, List<String>> categorySources;
	
	public CollectUserTweets() {
		super();
		this.categorySources = new TreeMap<String, List<String>>();
		Category[] categories = Category.getAllCategories();
		// you get 180 requests every 15 minutes
		for (Category category : categories) {
			String propName = "collect.category." + category.getName();
			String userNamesCSV = ConfigResources.getProperty(propName);
			// translate comma separated list into List<String>
			CSVReader reader = new CSVReader(new StringReader(userNamesCSV));
			try {
			    String[] userNames = reader.readNext();
			    List<String> userList = new ArrayList<String>();
			    if(null != userNames) {
					userList = new ArrayList<String>(Arrays.asList(userNames));
			    }
				categorySources.put(category.getName(), userList);
			} catch(IOException e) {
				log.severe("Could not read our collect.category.* lines correctly from default.properties");
				log.severe(e.getMessage());
				throw new IllegalStateException(e);
			}
		}
	}
	
	/**
	 * Try to get number of pages requested unless we start seeing duplicate content, then cancel out
	 */
	public void run() {
		AccessToken accessToken = new AccessToken("22074447-PnLWvTg8AsrCRPPRvpdsMKwHHzYcBWmbBKC1MHLZA", "m3L67gyjVV3t4ylEBTDLUePIMSvuw45uq7VSgA933zc");
	    Twitter twitter = Twitter4jUtil.getTwitterInstance();
	    twitter.setOAuthConsumer("qH1bauKaKWBJFKy1y75pBQ", "mpQG5tK0ZaHtoaBmZ7Kdn1u2TQE8UZi53sLF6Yo7g");
	    twitter.setOAuthAccessToken(accessToken);
		Iterator<String> keys = categorySources.keySet().iterator();
		// create a flag that signals if we are collecting for userNames yet
		boolean collect = false;
		if(null == skipToUserName) {
			collect = true;
		}
		
		while(keys.hasNext()) {
			String category = keys.next();
			List<String> userNames = categorySources.get(category);
			for (String userName : userNames) {
				// logic for turning on collect
				if (!collect && userName.equalsIgnoreCase(skipToUserName)) {
					log.info("Resuming collection with screenName:" + userName);
					collect = true;
				}
				
				// store Tweets if collect is true
				if(collect) {
				    try {
				    	Twitter4jUtil.storeTweetsByUser(twitter, userName, 10, Category.getCategoryByName(category));
				    } catch(TwitterException e) {
				    	log.warning("Error trying to get Tweets for: " + userName);
				    	log.warning(e.getErrorMessage());
				    	// let's wait for 15 minutes and resume for this screen user
				    	try {
				    		Thread.sleep(MINS_15);
					    	log.warning("Trying to resume for userName: " + userName);
					    	Twitter4jUtil.storeTweetsByUser(twitter, userName, 10, Category.getCategoryByName(category));
				    	} catch(InterruptedException ex) {
					    	throw new RuntimeException(ex.getMessage());
					    } catch(TwitterException ex) {
					    	throw new RuntimeException(ex);
				    	}
				    }
				}
			}
	    }
	}
	
	public String getSkipToUserName() {
		return skipToUserName;
	}

	public void setSkipToUserName(String skipToUserName) {
		this.skipToUserName = skipToUserName;
	}

	public static void main(String args[]) {
		ConfigResources.init();
		CollectUserTweets cut = new CollectUserTweets();
		//cut.setSkipToUserName("FoodNetwork");
		cut.run();

	}
}
