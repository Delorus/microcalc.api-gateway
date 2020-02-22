FROM openjdk:11-jre-slim
WORKDIR /app
COPY "./apiservice/build/libs/apiservice-0.0.1.jar" .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "apiservice-0.0.1.jar"]