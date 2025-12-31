FROM eclipse-temurin:25 AS builder
WORKDIR /app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew build --no-daemon

FROM eclipse-temurin:25
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]