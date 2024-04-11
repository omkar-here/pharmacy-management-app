FROM openjdk:17-jdk-slim-bullseye
WORKDIR /app
ENV PORT 8060
EXPOSE 8060
COPY target/*.jar /app/api-gateway.jar
ENTRYPOINT exec java $JAVA_OPT -jar api-gateway.jar