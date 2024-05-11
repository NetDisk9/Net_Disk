FROM openjdk:11
RUN mkdir -p /data/project
ADD email-service-1.0-SNAPSHOT.jar /data/project/email-service-1.0-SNAPSHOT.jar
EXPOSE 8082
ENTRYPOINT ["/bin/sh", "-c", "java -jar /data/project/email-service-1.0-SNAPSHOT.jar"]

