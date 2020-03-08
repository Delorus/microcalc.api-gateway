FROM gradle:jdk11 as build
WORKDIR /workdir
COPY ["gradlew", "settings.gradle", "build.gradle", "./"]
COPY ["apiservice/build.gradle", "apiservice/settings.gradle", "./apiservice/"]
COPY ["expr/build.gradle", "expr/settings.gradle", "./expr/"]
COPY gradle gradle
RUN ./gradlew --refresh-dependencies dependencies

COPY apiservice/src apiservice/src
COPY expr/src expr/src
RUN ./gradlew bootJar

FROM openjdk:11-jre-slim
ARG version=0.0.1
ENV VERSION=$version
WORKDIR /app
#COPY vmoptions vmoptions
COPY --from=build "/workdir/apiservice/build/libs/apiservice-${VERSION}.jar" .
RUN ln -s apiservice-${VERSION}.jar apiservice.jar
EXPOSE 8080
#ENTRYPOINT ["java", "@vmoptions", "-jar", "apiservice.jar"]
ENTRYPOINT ["java", "-jar", "apiservice.jar"]