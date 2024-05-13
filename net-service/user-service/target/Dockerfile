FROM openjdk:11
RUN mkdir -p /data/project
ADD user-service-1.0-SNAPSHOT.jar /data/project/user-service-1.0-SNAPSHOT.jar
ADD ip2region.xdb ip2region.xdb
RUN echo "Asia/Shanghai" > /etc/timezone
EXPOSE 8081
ENTRYPOINT ["/bin/sh", "-c", "java -jar /data/project/user-service-1.0-SNAPSHOT.jar"]
