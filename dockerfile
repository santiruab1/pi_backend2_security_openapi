FROM amazoncorretto:21-alpine-jdk

COPY target/pib2-0.0.1-SNAPSHOT.jar /api-v1.jar
ENTRYPOINT ["java","-jar","/api-v1.jar"]