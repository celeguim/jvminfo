FROM arm64v8/tomcat:10.0.26-jdk11
COPY ./build/jvminfo.war /usr/local/tomcat/webapps/
