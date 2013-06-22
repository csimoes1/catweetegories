package org.simoes.common;

import java.io.Serializable;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simoes.classify.Category;
import org.simoes.util.MongoDbUtil;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.json.DataObjectFactory;

import com.google.common.base.Strings;
import com.google.common.primitives.Longs;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class StatusPlus implements Comparable<StatusPlus>, Serializable {
	private static final long serialVersionUID = -524838199299209892L;

	private Status status;
	private Category category;
	private Long userId;
	private Date recatDate;
	private boolean userCategorized;
	private static Pattern PATTERN_HTTP;
	
	static {
		PATTERN_HTTP = Pattern.compile("(http[s]?://\\S+)");
	}
	
	public StatusPlus() {
		super();
	}
	
	/**
	 * First we remove our custom fields from DBObject so that the raw JSON for a Twitter4J Status object
	 * can be passed to Twitter4J
	 * 
	 * @param dbObject
	 * @throws TwitterException
	 */
	public StatusPlus(DBObject dbObject) throws TwitterException {
		// remove our added category field
		String categoryString = (String)dbObject.removeField(MongoDbUtil.FIELD_CATEGORY);
		if(Strings.isNullOrEmpty(categoryString)) {
			category = null;
		} else {
			category = Category.getCategoryByName(categoryString);
		}
		// try to remove userId and change date if they exist
		userId = (Long)dbObject.removeField(MongoDbUtil.FIELD_USER_ID);
		recatDate = (Date)dbObject.removeField(MongoDbUtil.FIELD_RECAT_DATE);
		Boolean userCat = (Boolean)dbObject.removeField(MongoDbUtil.FIELD_USER_CATEGORIZED);
		if(null != userCat) {
			userCategorized = userCat.booleanValue(); 
		}
		// remove MongoDB _id field
		dbObject.removeField(MongoDbUtil.FIELD_ID);
		
		// reconstruct Status object
		String rawJSON = dbObject.toString();
		status = DataObjectFactory.createStatus(rawJSON);
		
	}
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long a) {
		this.userId = a;
	}
	public Date getRecatDate() {
		return recatDate;
	}
	public void setRecatDate(Date a) {
		this.recatDate = a;
	}

	public boolean isUserCategorized() {
		return userCategorized;
	}

	public void setUserCategorized(boolean userCategorized) {
		this.userCategorized = userCategorized;
	}

	/**
	 * Returns the HTML for our Status including linkable URL's
	 * @return
	 */
	public String getStatusHTML() {
		StringBuilder result = new StringBuilder();
		result.append("<p class='status-text'>");
		if(null != status) {
			String temp = status.getText();
			String text = linkifyText(status, temp);
			result.append(text);
		}
		result.append("</p>");
		return result.toString();
	}
	
	/**
	 * outputs this object in MongoDB's DBObject format
	 * @return
	 */
	public DBObject toDBObject() {
		String rawJSON = DataObjectFactory.getRawJSON(status);
		DBObject dbObject = (DBObject)JSON.parse(rawJSON);
		if(null == dbObject) {
			throw new IllegalStateException("DataObjectFactory.getRawJSON() returned null, Twitter4J is not initialized properly.");
		}
		
		if(null == getCategory()) {
			dbObject.put(MongoDbUtil.FIELD_CATEGORY, null);
		} else {
			dbObject.put(MongoDbUtil.FIELD_CATEGORY, getCategory().getName());
		}
		// put recatUserId and recateDate
		dbObject.put(MongoDbUtil.FIELD_RECAT_DATE, recatDate);
		dbObject.put(MongoDbUtil.FIELD_USER_ID, userId);
		dbObject.put(MongoDbUtil.FIELD_USER_CATEGORIZED, userCategorized);
		
		// add _id field for MongoDB
		dbObject.put(MongoDbUtil.FIELD_ID, String.valueOf(status.getId()));
		return dbObject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((status == null) ? 0 : Longs.hashCode(status.getId()));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StatusPlus other = (StatusPlus) obj;
		if (status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (status.getId() != other.getStatus().getId()) {
			return false;
		}
		return true;
	}

	/**
	 * Sorts these Tweets in reverse order with highest numbers first
	 */
	@Override
	public int compareTo(StatusPlus o) {
		if(null == o) {
			throw new IllegalArgumentException("StatusPlus passed in was null");
		} else if(null == status || o.getStatus() == null) {
			throw new IllegalArgumentException("status object was null, so no Id field exists!");
		}
		if(status.getId() < o.getStatus().getId()) {
			return 1;
		} else if(status.getId() > o.getStatus().getId()) {
			return -1;
		}
		return 0;
	}
	
	/**
	 * If we find any "http(s)://" in our text turn then into links
	 * @param s
	 * @param t
	 * @return
	 */
	public String linkifyText(Status s, String t) {
		Matcher m = PATTERN_HTTP.matcher(t);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "<a href='$" + 1 + "' target='_blank'>$" + 1 + "</a>");
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	public static void main(String args[]) {
		String a = "Check out http://t.co/0eGy8oyaSP or http://t.co/0zWv2zOPyN";
		
		StatusPlus sp = new StatusPlus();
		
		String aResult = sp.linkifyText(null, a);
		System.out.println(aResult);
		
	}
}
