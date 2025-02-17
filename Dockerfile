FROM openjdk:21-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY src src
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests
FROM openjdk:21-jdk
VOLUME /tmp
COPY --from=build /app/target/*.jar desafio-backend-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/desafio-backend-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080