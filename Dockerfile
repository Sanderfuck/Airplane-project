FROM maven:3.8.5-openjdk-17-slim AS build
COPY ./pom.xml /airplane/pom.xml
COPY ./src /airplane/src
WORKDIR /airplane
RUN mvn clean package -DskipTests

FROM openjdk:17-alpine3.14
COPY --from=build /airplane/target/airplane-project.jar /home/target/airplane-project.jar
CMD java -jar /airplane/target/airplane-project.jar