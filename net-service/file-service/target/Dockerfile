FROM openjdk:11
RUN mkdir -p /data/project
ADD file-service-1.0-SNAPSHOT.jar /data/project/file-service-1.0-SNAPSHOT.jar
RUN echo "Asia/Shanghai" > /etc/timezone
EXPOSE 8083
ENTRYPOINT ["/bin/sh", "-c", "java -jar /data/project/file-service-1.0-SNAPSHOT.jar"]

