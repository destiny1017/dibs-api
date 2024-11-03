# 첫 번째 단계: Gradle 환경에서 Spring Boot 애플리케이션 빌드
FROM gradle:7.6.0-jdk17 AS build
WORKDIR /app

# 필요한 소스 및 설정 파일 복사
COPY . .

# bootJar 생성
RUN gradle bootJar

# 두 번째 단계: 실제 애플리케이션을 실행할 경량 JDK 이미지
FROM openjdk:17-alpine
#WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar ./dibs-api.jar

#FROM openjdk:17-alpine
#RUN gradle bootJar
#ARG JAR_FILE=./build/libs/*.jar
#COPY ${JAR_FILE} ./dibs-api.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "/dibs-api.jar"]