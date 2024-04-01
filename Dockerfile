FROM eclipse-temurin:17-jdk-alpine
COPY ./target/jvminfo-0.0.1-SNAPSHOT.jar /app.jar
ENV JAVA_OPTS="-Dpar1=val1"
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar
