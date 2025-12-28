FROM eclipse-temurin:25
COPY . .
RUN ./gradlew build --no-daemon
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]