FROM openjdk:8-jdk-alpine
RUN apk --no-cache add curl
ARG JAR_FILE=target/*.jar
ARG CONFIG_FILE=src/main/resources/application.yaml
ARG DB_FILE=src/main/resources/sample.db

COPY ${JAR_FILE} app.jar
COPY ${CONFIG_FILE} ./config/application.yaml
COPY ${DB_FILE} ./config/sample.db
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]