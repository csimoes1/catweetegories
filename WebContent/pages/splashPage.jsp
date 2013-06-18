<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sb" uri="/struts-bootstrap-tags" %>
<html>
<head>

    <title>Catweetegories</title>
    <meta name="description" content="Catweetegories - Sort your Tweets into catagories, use machince learning algorithms to classify your tweets" />
<%--    
    <link rel="canonical" href="http://www.xxx.com/" />
 --%>
    
</head>
	<body>
    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span12">
          <div class="hero-unit-tight">
            <h2>Welcome to Catweetegories</h2>
	        	<p> <!-- This writeup need a lot of visual work -->
	        	[We will help you organize your tweets into categories using a machine learning algorithm.
	        	Just sign in to Twitter and try it out.
						If you see a Tweet that is mis-categorized, please move it to the right category.
						Every time you help us correctly categorize a tweet our machine learning algorithm gets smarter.]
	        	</p>
          </div>
					<p>
						<a href="login">
							<div class="sign_in_with_twitter darker">
								<button>
									<div class="icon"></div>
									<div class="pull-right">Sign in with Twitter</div>
								</button>
							</div>
						</a>
					</p>
          
				</div>
			</div><!-- end of row-fluid -->
		</div><!-- end of container -->    
  </body>
</html>
