###############################
# Build Stage
FROM maven:3.8.5-openjdk-17 AS build_image
RUN git clone -b jvminfo-v2 https://github.com/celeguim/jvminfo.git
RUN cd jvminfo && mvn clean install

###############################
# Runtime Stage
FROM openjdk:17-jdk
COPY --from=build_image ./jvminfo/target/jvminfo*.jar /app.jar
EXPOSE 8080

ENV JAVA_OPTS="-Xms10m -Xmx20m -XX:+UseG1GC"
ENV JAR_ARGS="arg1=val1 arg2=val2"

ENTRYPOINT exec java -jar ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom /app.jar ${JAR_ARGS}
