package org.simoes.util;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.simoes.common.StatusPlus;
import org.simoes.servlet.dao.TwitterUser;

import twitter4j.TwitterException;

import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;


/**
 * Call getInstance to access this class, it is meant to be a singleton
 * 
 * @author csimoes
 * 
 */
public class MongoDbUtil {
	static Logger log = Logger.getLogger(MongoDbUtil.class.getName());
	private static MongoDbUtil INSTANCE = new MongoDbUtil();
	
	public static final String DB = "catweetegories";
	public static final String COLL_COLLECT = "collect";
	public static final String COLL_STATUS = "statuses";
	public static final String COLL_USER = "users";
	public static final String FIELD_ACCESS_TOKEN = "accessToken";
	public static final String FIELD_ACCESS_TOKEN_SECRET = "accessTokenSecret";
	public static final String FIELD_CATEGORY = "category";
	public static final String FIELD_USER_CATEGORIZED = "ct_user_categorized"; // did a user categorize this tweet
	public static final String FIELD_ID = "_id";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_RECAT_DATE = "ct_recat_date";
	public static final String FIELD_USER_ID = "ct_user_id";
	public static final String FIELD_SCREEN_NAME = "screenName";
	

	private Mongo client;

	public static MongoDbUtil getInstance() {
		return INSTANCE;
	}

	private MongoDbUtil() {
		try {
			this.client = new Mongo("localhost", 27017);
		} catch (IOException e) {
			log.severe(e.getMessage());
		}
	}
	
	/**
	 * Inserts a User object into our user collection
	 */
	public void insertUserRecord(String token, String tokenSecret, TwitterUser user) {
		if(null == user) {
			throw new IllegalArgumentException("TwitterUser was null");
		} else if(Strings.isNullOrEmpty(token)) {
			throw new IllegalArgumentException("access token was null/empty");
		} else if(Strings.isNullOrEmpty(tokenSecret)) {
			throw new IllegalArgumentException("access tokenSecret was null/empty");
		}
		
		DB db = client.getDB(DB);
		db.requestStart();
		try {
			DBCollection coll = db.getCollection(COLL_USER);
			BasicDBObject doc = new BasicDBObject(FIELD_ID, user.getId());
			doc.append(FIELD_NAME, user.getName());
			doc.append(FIELD_SCREEN_NAME, user.getScreenName());
			doc.append(FIELD_ACCESS_TOKEN, token);
			doc.append(FIELD_ACCESS_TOKEN_SECRET, tokenSecret);
			coll.insert(doc);
		} finally {
			db.requestDone();
		}
		
	}
	
	/**
	 * Inserts the Statuses passed in to the DB
	 * @param statusJSONs
	 */
	public void insertStatuses(String collection, List<StatusPlus> statusPluses) {
		DB db = client.getDB(DB);
		db.requestStart();
		try {
			DBCollection coll = db.getCollection(collection);
			DBObject[] inserts = new DBObject[statusPluses.size()];
			for (int i = 0; i < statusPluses.size(); i++) {
				StatusPlus sp = statusPluses.get(i);
				try {
				inserts[i] = sp.toDBObject();
				} catch(NullPointerException e) {
					log.severe(e.getMessage());
				}
			}
			
			coll.insert(inserts);
		} finally {
			db.requestDone();
		}
	}
	
	/**
	 * Updates the tweets category, and records that this tweet has been "correctly" categorized by a user
	 * @param id
	 * @param newCategory
	 */
	public void updateTweetCategory(Long userId, String tweetId, String newCategory) {
		DB db = client.getDB(DB);
		db.requestStart();
		try {
			DBCollection coll = db.getCollection(COLL_STATUS);
			
			BasicDBObject updateQuery = new BasicDBObject();
			BasicDBObject updateFields = new BasicDBObject().append(FIELD_CATEGORY, newCategory);
			updateFields.append(FIELD_RECAT_DATE, Calendar.getInstance().getTime());
			updateFields.append(FIELD_USER_CATEGORIZED, true);
			updateFields.append(FIELD_USER_ID, userId);
			updateQuery.append("$set", updateFields);
		 
			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.append(FIELD_ID, tweetId);
		 
			coll.update(searchQuery, updateQuery);		
			
			//TODO: Also add this tweet to the "Categorized" collection in Turk
		} finally {
			db.requestDone();
		}
	}
	
	public StatusPlus loadStatusPlus(String id) throws TwitterException {
		DB db = client.getDB(DB);
		db.requestStart();
		try {
			DBCollection coll = db.getCollection(COLL_STATUS);
			BasicDBObject query = new BasicDBObject(FIELD_ID, id);
			DBObject dbObject = coll.findOne(query);
			StatusPlus result = new StatusPlus(dbObject);
			return result;
		} finally {
			db.requestDone();
		}
	}

	/**
	 * Loads the number of StatusPluses specified by the limit parameter.  If limit is 0 then we return all results.
	 * @param limit
	 * @return
	 * @throws TwitterException
	 */
	public SortedSet<StatusPlus> loadAllStatusPluses(String collName, int limit) throws TwitterException {
		TreeSet<StatusPlus> result = new TreeSet<StatusPlus>();
		DB db = client.getDB(DB);
		db.requestStart();
		try {
			DBCollection coll = db.getCollection(collName);
			DBCursor dbCursor = null;
			if(limit <= 0) {
				dbCursor = coll.find(); 
			} else {
				dbCursor = coll.find().limit(limit); 
			}
			
			while(dbCursor.hasNext()) {
				DBObject obj = dbCursor.next();
				StatusPlus sp = new StatusPlus(obj);
				result.add(sp);
			}

			return result;
		} finally {
			db.requestDone();
		}
	}
}
