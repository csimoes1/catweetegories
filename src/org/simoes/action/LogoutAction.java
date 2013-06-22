package org.simoes.action;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.interceptor.SessionAware;
import org.simoes.util.Constants;

import com.opensymphony.xwork2.ActionSupport;

/**
 * <code>Set welcome message.</code>
 */
public class LogoutAction extends ActionSupport implements SessionAware {
	static Logger log = Logger.getLogger(LogoutAction.class.getName());
	
	private Map<String, Object> userSession;
	
    public String execute() throws Exception {
    	
        userSession.remove(Constants.SESSION_TWITTER_OBJ);
        userSession.remove(Constants.SESSION_TWITTER_USER);
    	
        return SUCCESS;
    }
    
    public void setSession(Map<String, Object> session) {
        userSession = session ;
    }

}
