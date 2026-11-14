###############################
# Build Stage
# FROM maven:3.8.5-openjdk-17 AS build_image
# Replace the old, buggy image
FROM maven:3.9.9-eclipse-temurin-17 AS build_image
RUN git clone -b jvminfo-v11 https://github.com/celeguim/jvminfo.git
RUN cd jvminfo && mvn clean install

###############################
# Runtime Stage
FROM eclipse-temurin:17-jre
COPY --from=build_image ./jvminfo/target/jvminfo*.jar /app.jar
EXPOSE 8080

ENV JAVA_OPTS="-Xms100m -Xmx200m -XX:+UseG1GC"
ENV JAR_ARGS="par1=jvminfov11 par2=val2"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar $JAR_ARGS"]
# CMD ["arg1=val1", "arg2=val2"]
