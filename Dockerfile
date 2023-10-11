#
# Build stage
#
FROM maven:3.9.4-eclipse-temurin-17-alpine AS build
COPY src /src
COPY pom.xml .
RUN mvn -f ./pom.xml -DskipTests=true clean package


#
# Package stage
#
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080/tcp
ENTRYPOINT ["java","-jar","app.jar"]