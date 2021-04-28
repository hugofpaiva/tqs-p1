FROM maven:3.8.1-adoptopenjdk-11

WORKDIR /app

COPY . /app/

RUN mvn -DskipTests clean package

ENTRYPOINT ["java","-jar","/app/target/air-quality-service-0.0.1-SNAPSHOT.jar"]