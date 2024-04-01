# jvminfo

Simple java application to get and print docker/server name


```shell
RUN

docker run -e JAVA_OPTS="-Xms64m -Xmx128m" -d -p 8080:8080 celeguim/jvminfo:v3


```

```shell
TEST

[http://localhost:8080/jvminfo/](http://localhost:8080/jvminfo/)
```

```shell
BUILD

docker build -t celeguim/jvminfo:v3 .

docker push celeguim/jvminfo:v3
```
