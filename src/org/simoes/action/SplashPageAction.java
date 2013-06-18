package org.simoes.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionSupport;

/**
 * <code>Set welcome message.</code>
 */
public class SplashPageAction extends ActionSupport {
	private static final long serialVersionUID = -7125846510144886839L;
	private static final Log log = LogFactory.getLog(SplashPageAction.class);
	
    public String execute() throws Exception {
        setMessage(getText(MESSAGE));
        
        return SUCCESS;
    }

	/**
     * Provide default value for Message property.
     */
    public static final String MESSAGE = "HelloWorld.message";

    /**
     * Field for Message property.
     */
    private String message;

    /**
     * Return Message property.
     *
     * @return Message property
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set Message property.
     *
     * @param message Text to display on HelloWorld page.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
