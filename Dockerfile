# Stage 1: build với Maven
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app                                  
COPY pom.xml .
# Tải dependency trước để tận dụng cache khi code thay đổi
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: chạy ứng dụng
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]