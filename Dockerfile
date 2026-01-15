FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle* settings.gradle* ./

RUN chmod +x gradlew && sed -i 's/\r$//' gradlew

COPY . .
RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

