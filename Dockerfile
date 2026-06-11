FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /builder
ARG JAR_FILE=target/templates-poc-exec.jar
COPY ${JAR_FILE} application.jar
RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:25-jdk-alpine
WORKDIR /application
COPY --from=builder /builder/extracted/dependencies/ ./
COPY --from=builder /builder/extracted/spring-boot-loader/ ./
COPY --from=builder /builder/extracted/snapshot-dependencies/ ./
COPY --from=builder /builder/extracted/application/ ./

EXPOSE 9000
EXPOSE 9001

ENTRYPOINT ["java", "-jar", "application.jar"]