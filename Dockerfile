FROM openjdk:8-jdk-alpine
ARG PORT_NUMBER=8081
EXPOSE $PORT_NUMBER
ARG JAR_FILE=build/libs/codecompiler-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} codecompiler.jar
ENTRYPOINT ["java", "-jar","/codecompiler.jar"]