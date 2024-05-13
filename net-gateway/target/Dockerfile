FROM openjdk:11
RUN mkdir -p /data/project
ADD net-gateway-1.0-SNAPSHOT.jar /data/project/net-gateway-1.0-SNAPSHOT.jar
RUN echo "Asia/Shanghai" > /etc/timezone
EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "java -jar /data/project/net-gateway-1.0-SNAPSHOT.jar"]

