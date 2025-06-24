FROM eclipse-temurin:21-jdk-alpine as builder
WORKDIR application
ARG JAR_FILE=target/templates-poc-exec.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:21-jdk-alpine
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/application/ ./

EXPOSE 9000
EXPOSE 9001

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]