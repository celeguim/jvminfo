# jvminfo

Simple java application to create an HTML Table with the most important JVM properties


```shell
# RUN

docker run \
-e JAVA_OPTS="-Xms10m -Xmx20m -XX:+UseG1GC" \
-e JAR_ARGS="par1=val1 par2=val2" \
-d -p 8080:8080 celeguim/jvminfo:v3

```

###### TEST </br>[http://localhost:8080/jvminfo/](http://localhost:8080/)

```shell
# BUILD

# build for single platform
docker build -t celeguim/jvminfo:v4 .
docker push celeguim/jvminfo:v4

# build for multi platform
# create the builder as docker-container
docker buildx create --name container --driver=docker-container

# build image for arm64 and amd64
docker buildx build  --tag celeguim/jvminfo:v4  --platform linux/arm64/v8,linux/amd64  --builder container  --push .
docker buildx build  --tag celeguim/jvminfo:latest  --platform linux/arm64/v8,linux/amd64  --builder container  --push .


```
