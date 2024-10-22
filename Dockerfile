FROM kulbhushanmayer/tomcat:10.1.30
COPY target/*.war /tomcat/webapps/welcome.war
