package org.simoes.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractCustomServlet extends HttpServlet implements Filter {
    private static final long serialVersionUID = 1L;

    @Override
	public void init(FilterConfig arg0) throws ServletException {

    }

    public void doFilter(ServletRequest servletrequest, ServletResponse servletresponse, 
            FilterChain arg2) throws IOException, ServletException 
    { 
    	this.service((HttpServletRequest)servletrequest, (HttpServletResponse)servletresponse);
    } 

} 

