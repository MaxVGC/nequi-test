FROM eclipse-temurin:21-alpine

ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG DB_DDL_AUTO

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV DB_DDL_AUTO=${DB_DDL_AUTO}


WORKDIR /app

RUN apk add --no-cache maven

COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./
COPY src ./src

RUN chmod +x mvnw && ./mvnw clean package -Dmaven.test.skip=true

RUN cp target/*.jar /app.jar

VOLUME /tmp
EXPOSE 8080
EXPOSE 8081

# Ejecuta la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=prod"]