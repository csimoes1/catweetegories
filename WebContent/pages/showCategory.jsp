<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sb" uri="/struts-bootstrap-tags" %>
<html>
<head>

    <title>Catweetegories - ${category}</title>
    <meta name="description" content="Catweetegories - Your tweets sorted into categories based on our classifier." />
<%--    
    <link rel="canonical" href="http://www.xxx.com/" />
 --%>
    
</head>
	<body>

    <div class="container-fluid">
      <h3>${category} - ${twitterUser.screenName}</h3>

			<!-- Output tweets here -->
			<s:iterator status="stat" value="statuses">
      <div class="row-fluid show-grid tweet-padding">
      		<!-- Tweet id is the id of this DIV -->
	        <div class="span7 ${category.name}" id="${status.id}">
						<div class="media">
						  <a class="pull-left" href="#">
						    <img class="media-object" src="${status.user.profileImageURL}">
						  </a>
						  <div class="media-body">
									<p>${status.text}</p>
						  <!-- 
						    <h4 class="media-heading">Media heading</h4>
						     -->
						  </div>
						</div>	        
					</div>
	
	        <div class="span2">
							<div class="dropdown">
								<a class="dropdown-toggle" id="dLabel" role="button"
										data-toggle="dropdown" data-target="#" href="/showTweets">
										Recategorize <b class="caret"></b>
								</a>
								<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
								<s:iterator status="cat" value="categories">
									<li><a href="#" class="recategorize">${name}</a></li>
								</s:iterator>
								</ul>
							</div>
					</div>
	        <div class="span3">
					</div>
			</div><!-- end of row-fluid -->
			</s:iterator>
		</div><!-- end of container -->
  </script>
		    
  </body>
  
</html>
