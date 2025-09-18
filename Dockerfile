FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/vending-machine-api-0.0.1.jar /app/vending-machine-api.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/vending-machine-api.jar", "-web -webAllowOthers -tcp -tcpAllowOthers -browser"]