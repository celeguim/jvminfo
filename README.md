# jvminfo

Simple java application to get and print docker/server name


```shell
RUN

docker run -d -p 8080:8080 celeguim/jvminfo:v2
```

```shell
TEST

http://localhost:8080/jvminfo
```

```shell
BUILD

docker build . -t jvminfo

docker images

docker run -d -p 8080:8080 jvminfo:latest

celeguim/jvminfo:arm64v8

docker tag 8d6a091d39a1 celeguim/jvminfo:v2

docker push celeguim/jvminfo:v2
```
