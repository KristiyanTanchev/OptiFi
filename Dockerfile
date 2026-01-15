FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .

# tests already handled by CI
RUN ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
