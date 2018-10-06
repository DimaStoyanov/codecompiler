FROM openjdk:8-jdk-alpine
ARG file_path
ADD $file_path Main.java
ENTRYPOINT ["javac", "Main.java"]
#ENTRYPOINT ["java", "HelloWorld"]