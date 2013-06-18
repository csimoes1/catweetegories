package org.simoes.action;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.interceptor.SessionAware;
import org.simoes.servlet.dao.TwitterUser;
import org.simoes.util.Constants;
import org.simoes.util.MongoDbUtil;

import com.opensymphony.xwork2.ActionSupport;

/**
 * <code>Set welcome message.</code>
 */
public class Recategorize extends ActionSupport implements SessionAware {
	private static final long serialVersionUID = 5085481084146875167L;
	static Logger log = Logger.getLogger(Recategorize.class.getName());
	/*
	 * The following block of code gives us access to the Session
	 */
	private Map<String, Object> userSession;
    public void setSession(Map<String, Object> session) {
        userSession = session ;
    }

	private String id;
	private String newCategory;
	
    public String execute() throws Exception {
        TwitterUser twitterUser = (TwitterUser)userSession.get(Constants.SESSION_TWITTER_USER);
    	if(null == twitterUser) {
    		log.severe("twitterUser was not found in the Session");
    		throw new IllegalAccessException("TwitterUser is required in the Session to make this call");
    	}
    	
        // call database and update category for this id
    	MongoDbUtil.getInstance().updateTweetCategory(twitterUser.getId(), id, newCategory);
    	
        return SUCCESS;
    }
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNewCategory() {
		return newCategory;
	}

	public void setNewCategory(String newCategory) {
		this.newCategory = newCategory;
	}

}
