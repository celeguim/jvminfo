FROM openjdk:8-jdk
MAINTAINER <luiz.celeguim@gmail.com>
EXPOSE 8080
VOLUME /data
COPY target/jvminfo-0.0.1-SNAPSHOT.jar /app/
CMD ["java", "-jar", "/app/jvminfo-0.0.1-SNAPSHOT.jar"]
