#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:8-jdk
MAINTAINER <luiz.celeguim@gmail.com>
VOLUME /data
ARG JAR_FILE
ENV _JAVA_OPTIONS "-Xms256m -Xmx512m -Djava.awt.headless=true"
EXPOSE 8080
COPY --from=build /home/app/target/jvminfo-0.0.1-SNAPSHOT.jar /opt/app.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/opt/app.jar"]
# COPY target/jvminfo-0.0.1-SNAPSHOT.jar /app/
# COPY ${JAR_FILE} /opt/jvminfo.jar
# CMD ["java", "-jar", "/app/jvminfo-0.0.1-SNAPSHOT.jar"]




