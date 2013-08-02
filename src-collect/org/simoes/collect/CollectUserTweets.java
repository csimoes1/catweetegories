package org.simoes.collect;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.simoes.classify.Category;
import org.simoes.util.ConfigResources;
import org.simoes.util.Twitter4jUtil;

import com.google.common.base.Strings;

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
	private TreeMap<String, List<String>> liftSources; //user names for our Lift categories
	
	public CollectUserTweets() {
		super();
		log.warning("CollectUserTweets STARTED");
		this.categorySources = new TreeMap<String, List<String>>();
		this.liftSources = new TreeMap<String, List<String>>();

		Category[] categories = Category.getAllCategories();
		// you get 180 requests every 15 minutes
		for (Category category : categories) {
			// get collect twitter user accounts, these are used to create our classifier
			String propName = "collect.category." + category.getName();
			String userNamesCSV = ConfigResources.getProperty(propName);
			// translate comma separated list into List<String>
			CSVReader readerCollect = new CSVReader(new StringReader(userNamesCSV));

			// get lift twitter user accounts, these are used to improve an existing classifier
			String propLiftName = "lift.category." + category.getName();
			String userLiftNamesCSV = ConfigResources.getProperty(propLiftName);
			// translate comma separated list into List<String>
			CSVReader readerLift = new CSVReader(new StringReader(userLiftNamesCSV));
			try {
				// populate categorySources with all of our collect accounts
			    String[] userNamesCollect = readerCollect.readNext();
			    List<String> userListCollect = new ArrayList<String>();
			    if(null != userNamesCollect) {
					userListCollect = new ArrayList<String>(Arrays.asList(userNamesCollect));
			    }
				categorySources.put(category.getName(), userListCollect);
				
				// populate liftSources with all lift accounts that are not already in categorySources
				// also exclude any that are already in liftSources, but report the error
			    List<String> userListLift = new ArrayList<String>();
			    String[] userNamesLift = readerLift.readNext();
			    if(null != userNamesLift) {
				    for (String s : userNamesLift) {
				    	// if this is not found already in categorySources or liftSources then add it
				    	
						if(categorySourcesContains(s)) {
							log.warning("Duplicate account, lift account already exists in categorySources: " + s);
						} else {
							if(liftSourcesContains(s)) {
								log.warning("Duplicate account, lift account already exists in liftSources: " + s);
							} else {
								userListLift.add(s);
								log.finest("Added " + s);
							}
						}
					}
			    }
			    log.info("Final userLiftList: " + userListLift);
			    
				liftSources.put(category.getName(), userListLift);
				
			} catch(IOException e) {
				log.severe("Could not read our collect.category.* lines correctly from default.properties");
				log.severe(e.getMessage());
				throw new IllegalStateException(e);
			}
		}
	}
	
	/**
	 * Returns true if any of the categorySources values match account ignoring case 
	 * @param account
	 * @return
	 */
	private boolean categorySourcesContains(String account) {
		if(!Strings.isNullOrEmpty(account)) {
			Collection<List<String>> lists = categorySources.values();
			for (List<String> list : lists) {
				for (String a : list) {
					if(account.equalsIgnoreCase(a)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean liftSourcesContains(String account) {
		if(!Strings.isNullOrEmpty(account)) {
			Collection<List<String>> lists = liftSources.values();
			for (List<String> list : lists) {
				for (String a : list) {
					if(account.equalsIgnoreCase(a)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	/**
	 * Try to get number of pages requested unless we start seeing duplicate content, then cancel out
	 */
	public void run() {
		AccessToken accessToken = new AccessToken("22074447-PnLWvTg8AsrCRPPRvpdsMKwHHzYcBWmbBKC1MHLZA", "m3L67gyjVV3t4ylEBTDLUePIMSvuw45uq7VSgA933zc");
	    Twitter twitter = Twitter4jUtil.getTwitterInstance();
	    twitter.setOAuthConsumer("qH1bauKaKWBJFKy1y75pBQ", "mpQG5tK0ZaHtoaBmZ7Kdn1u2TQE8UZi53sLF6Yo7g");
	    twitter.setOAuthAccessToken(accessToken);

	    collectSources(twitter, categorySources, false);
	    collectSources(twitter, liftSources, true);
	}
	
	/**
	 * 
	 * @param twitter
	 * @param sources
	 * @param lift - if false use categorySources (collect), if true use liftSources
	 */
	private void collectSources(Twitter twitter, TreeMap<String, List<String>> sources, boolean lift) {
		if(lift) {
			log.info("Processing Lift sources");
		} else {
			log.info("Processing Collect sources");
		}
		Iterator<String> keys = sources.keySet().iterator();
		// create a flag that signals if we are collecting for userNames yet
		
		while(keys.hasNext()) {
			String category = keys.next();
			List<String> userNames = sources.get(category);
			for (String userName : userNames) {
				// logic for turning on collect
			    try {
			    	if(lift) {
				    	Twitter4jUtil.storeLiftTweetsByUser(twitter, userName, 10, Category.getCategoryByName(category));
			    	} else {
				    	Twitter4jUtil.storeTweetsByUser(twitter, userName, 10, Category.getCategoryByName(category));
			    	}
			    } catch(TwitterException e) {
			    	log.warning("Error trying to get Tweets for: " + userName);
			    	log.warning(e.getErrorMessage());
			    	// let's wait for 15 minutes and resume for this screen user
			    	try {
			    		Thread.sleep(MINS_15);
				    	log.warning("Trying to resume for userName: " + userName);
				    	if(lift) {
					    	Twitter4jUtil.storeLiftTweetsByUser(twitter, userName, 10, Category.getCategoryByName(category));
				    	} else {
					    	Twitter4jUtil.storeTweetsByUser(twitter, userName, 10, Category.getCategoryByName(category));
				    	}
			    	} catch(InterruptedException ex) {
				    	throw new RuntimeException(ex.getMessage());
				    } catch(TwitterException ex) {
				    	throw new RuntimeException(ex);
			    	}
			    }
			}
	    }
		
	}
	
	
	public static void main(String args[]) {
		ConfigResources.init();
		CollectUserTweets cut = new CollectUserTweets();
		cut.run();

	}
}
