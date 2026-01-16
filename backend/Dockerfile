FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY . .

# Fix permissions + possible CRLF after copy
RUN chmod +x gradlew && sed -i 's/\r$//' gradlew

# Build runnable jar (skip tests; CI runs them)
RUN ./gradlew --no-daemon clean bootJar -x test

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
