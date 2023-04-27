FROM openjdk:17-jdk-alpine
COPY target/consumer-1.0-SNAPSHOT.jar consumer-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/consumer-1.0-SNAPSHOT.jar"]
