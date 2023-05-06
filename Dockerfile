FROM eclipse-temurin:17-jdk-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
VOLUME /tmp
COPY production/transport.jar app.jar
CMD exec java -jar app.jar
