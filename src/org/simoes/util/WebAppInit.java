package org.simoes.util;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class WebAppInit extends HttpServlet {
	Logger log = Logger.getLogger(WebAppInit.class.getName());

	private static final long serialVersionUID = 42L;
	

	public void init() {
		final String METHOD = "init(): ";
        ConfigResources.init(getInitParameter("init-file"));
        log.info(METHOD + "ConfigResources initialized.");
        // initialize objects needed for website
        // add metro areas list to ServletContext for all to see
//        this.getServletContext().setAttribute(Constants.SERVLET_CONTEXT_METRO_AREAS, ConfigResources.getMetroAreas());
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		
	}
}
