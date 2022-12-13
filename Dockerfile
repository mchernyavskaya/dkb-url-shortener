FROM openjdk:17-alpine

EXPOSE 8081
RUN mkdir "app"

WORKDIR /app

COPY build/libs/dkb-url-shortener-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=docker", "app.jar"]