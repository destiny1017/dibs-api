FROM openjdk:17-alpine
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} ./dibs-api.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=local", "/dibs-api.jar"]