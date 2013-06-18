package org.simoes.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.simoes.servlet.dao.TwitterUser;
import org.simoes.util.MongoDbUtil;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.RequestToken;

import com.google.common.base.Strings;

public class CallbackServlet extends AbstractCustomServlet {
    private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(CallbackServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	User user = null;
    	TwitterUser twitterUser = null;
        Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
        RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
        String verifier = request.getParameter("oauth_verifier");
        if(Strings.isNullOrEmpty(verifier)) {
        	log.warning("verifier was null, it is likely that user did not authorize us for their twitter account");
        } else {
            try {
                twitter.getOAuthAccessToken(requestToken, verifier);
                request.getSession().removeAttribute("requestToken");
    			user = twitter.showUser(twitter.getId());
    			twitterUser = new TwitterUser(user.getId(),user.getName(),user.getScreenName(),user.getLocation(),
    					user.getDescription(),user.getProfileImageURL().toString(),user.getProfileBackgroundImageUrl().toString(),
    					user.getFollowersCount(),user.getFriendsCount(),user.getFavouritesCount(),user.getStatusesCount(),
    					user.getListedCount(),user.getCreatedAt().toString(),user.getTimeZone(),user.getUtcOffset(),
    					user.getLang(),user.getURL()!=null?user.getURL().toString():null);
    			request.getSession().setAttribute("twitterUser", twitterUser);
    			
    			// add user to MongoDB if not already there
                String token = requestToken.getToken();
                String tokenSecret = requestToken.getTokenSecret();
    			MongoDbUtil.getInstance().insertUserRecord(token, tokenSecret, twitterUser);
            } catch (TwitterException e) {
            	log.severe(e.getMessage());
                throw new ServletException(e);
            }
        }
        response.sendRedirect(request.getContextPath() + "/categories");
    }
    
}
