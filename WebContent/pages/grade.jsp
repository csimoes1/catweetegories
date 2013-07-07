<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sb" uri="/struts-bootstrap-tags" %>
<html>
<head>

    <title>Tweet Statistics</title>
    <meta name="description" content="Statistics describing why this tweet was classified the way it was" />
<%--    
    <link rel="canonical" href="http://www.xxx.com/" />
 --%>
    
</head>
	<body>

    <div class="container">
    	<div class="row">
 	      <span class="pull-right"><a href="showCategory?category=${statusPlus.category.name}">Back to ${statusPlus.category.displayName}</a></span>
        <div class="span5" id="${statusPlus.status.id}">
	 	      <h4>Original Tweet (Category: ${statusPlus.category.displayName})</h4>
					<div class="media">
					  <a class="pull-left" href="#">
					    <img class="media-object img-rounded" src="${statusPlus.status.user.profileImageURL}">
					  </a>
					  <div class="media-body">
							${statusPlus.statusHTML}
						</div>
					</div>
  			</div>
  		</div>
    	<div class="row">
        <div class="span5">
		      <h4>Text used for categorization</h4>
		      ${statusPlus.classifyText}
		    </div>
    	</div>
    	<div class="row">
        <div class="span5">
		      <h4>Scoring for each Category</h4>
		      	<table class="table table-hovered">
						<s:iterator status="stat" value="categoryScores">
							<tr>
								<td>${key.displayName}</td>
								<td>${value}</td>
							</tr>
						</s:iterator>
		      	</table>
		    </div>
    	</div>
    	<div class="row">
        <div class="span12">
		      <h4>Meaningful words</h4>
		      <pre>${scoringTable}</pre>
		    </div>
    	</div>
		</div>				
  </script>
		    
  </body>
  
</html>
