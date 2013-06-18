<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sb" uri="/struts-bootstrap-tags" %>
<html>
<head>

    <title>Catweetegories - Tweets</title>
    <meta name="description" content="Catweetegories - Your tweets sorted into categories based on our classifier." />
<%--    
    <link rel="canonical" href="http://www.xxx.com/" />
 --%>
    
</head>
	<body>

    <div class="container-fluid">
      <h3>Select a Category</h3>
      <div class="row-fluid">
      	<div class="span3">
					<s:iterator status="stat" value="categories">
						<div class="show-grid">
							<a href="showCategory?category=${name}">${displayName}</a>
						</div>
      		<hr />
					</s:iterator>
				</div>      	
			</div>
      <div class="row-fluid">
      	<div class="span3">
					<div class="show-grid">
						<a href="showTweets">All Tweets</a>
					</div>
				</div>
			</div>      	
		</div><!-- end of container -->
  </script>
		    
  </body>
  
</html>
