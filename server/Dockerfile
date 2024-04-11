FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp

COPY ./target/server-0.0.1-SNAPSHOT.jar micorblog.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/micorblog.jar"]