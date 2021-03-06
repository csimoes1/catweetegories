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

    <div class="container">
      <h2>${twitterUser.screenName} tweets</h2>
      <div class="row-fluid">
      	<div class="span12">
 					<div class="btn-group">
						<s:iterator status="cat" value="categories">
							<button class="btn btn-${name}">${displayName}</button>
						</s:iterator>
						<button class="btn btn-all">All</button>
					</div>
				</div>
      	<div class="span12">
				</div>
			</div>
			<!-- Output tweets here -->
			<s:iterator status="stat" value="statuses">
      <div class="row show-grid tweet-padding">
    		<!-- Tweet id is the id of this DIV -->
        <div class="span5 ${category.name}" id="${status.id}">
					<div class="media">
					  <a class="pull-left" href="#">
					    <img class="media-object img-rounded" src="${status.user.profileImageURL}">
					  </a>
					  <div class="media-body">
							${statusHTML}
							<div id="tweet-actions">
								<a href="https://twitter.com/intent/tweet?in_reply_to=${status.id}">
								<img src="assets/image/reply.png" />
								Reply
								</a>
								<a href="https://twitter.com/intent/retweet?tweet_id=${status.id}">
								<img src="assets/image/retweet.png" />
								Retweet
								</a>
								<a href="https://twitter.com/intent/favorite?tweet_id=${status.id}">
								<img src="assets/image/favorite.png" />
								Favorite
								</a>
								<span class="pull-right"> 
								<a href="/showTweetStats?statusId=${status.id}">
								<i class="icon-question-sign"></i> Stats
								</a>
								</span>
				  		</div>
						</div>	        
					</div>
				</div>

				<div class="span2 dropdown">
						
						<a class="dropdown-toggle" id="dLabel" role="button" data-toggle="dropdown" data-target="#" href="/showTweets">
							<i class="icon-th-large"></i>
							Recategorize
						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="dLabel">
						<s:iterator status="cat" value="categories">
							<li role="presentation"><a role="menuitem" tabindex="-1" href="#" class="recategorize">${displayName}</a></li>
						</s:iterator>
						</ul>
				</div>
	
			</div><!-- end of row-fluid -->
			
			</s:iterator>
		</div><!-- end of container -->
  </script>
		    
  </body>
  
</html>
