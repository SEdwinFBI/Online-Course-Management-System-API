
FROM gradle:8.10.2-jdk21 AS builder

WORKDIR /app


COPY . .


RUN gradle clean bootJar --no-daemon


FROM eclipse-temurin:21-jdk


ENV SPRING_PROFILES_ACTIVE=prod \
    NAME_POSTGRESQL_DB=course_admin_db \
    URI_POSTGRESQL=jdbc:postgresql://localhost:5432 \
    USER_POSTGRESQL=postgres \
    PASSWORD_POSTGRESQL=postgres \
    MONGO_DB=mongodb://localhost:27017/course_admin \
    SECRET_JWT=change-secret

WORKDIR /app


COPY --from=builder /app/build/libs/*.jar app.jar


EXPOSE 8080

# Comando
ENTRYPOINT ["java", "-jar", "app.jar"]
