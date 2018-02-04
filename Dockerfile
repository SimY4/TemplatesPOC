FROM openjdk:8-jre-alpine

COPY target/templates-poc.jar .

EXPOSE 9000
EXPOSE 9001

CMD [ "java", "-jar", "templates-poc.jar" ]