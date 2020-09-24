FROM openjdk:11-jdk
MAINTAINER <luiz.celeguim@gmail.com>
EXPOSE 8081
ENV _JAVA_OPTIONS "-Xms256m -Xmx512m -Djava.awt.headless=true"
VOLUME /data
COPY target/jvminfo-0.0.1-SNAPSHOT.jar /app/jvminfo.jar
CMD ["java", "-jar", "/app/jvminfo.jar"]
