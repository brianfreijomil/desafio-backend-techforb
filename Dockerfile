FROM openjdk:21-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY src src
# Copia el wrapper de Maven
COPY mvnw .
COPY .mvn .mvn
# Establece permisos de ejecución para el wrapper de Maven
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests
# Etapa 2: Crea la imagen Docker final usando OpenJDK 19
FROM openjdk:19-jdk
VOLUME /tmp
# Copia el JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080