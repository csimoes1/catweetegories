catweetegories
==============



To run you will need:
- MongoDB installed locally on your box.
- Tomcat 7.x installed on your box
   - Be sure that the compiled source files from the catweetegories project go into the directory: "WebContent/WEB-INF/classes"
   - Also the contents of the conf directory has to end up in "WebContent/WEB-INF/classes" (we made the conf directory a source directory for our build)
   - Go to your Tomcat home directory
   - cd to the directory "conf/Catalina/localhost"
   - create a file called "ROOT.xml" and put the following text into the file 

&lt;Context path="/catweetegories" reloadable="true"  
         docBase="/Users/csimoes/workspace-juno/catweetegories/WebContent" 
         workDir="/Users/csimoes/workspace-juno/catweetegories/work" 
         debug="0" 
         privileged="true" 
         allowLinking="true"&gt;
&lt;/Context&gt;
   - You will need to update the docBase and workDir parameters to match the locations of where you checked out the catweetegories project from GitHub 
