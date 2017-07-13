FROM openjdk:8-jre-alpine

COPY target/POC.jar .

EXPOSE 9000
EXPOSE 9001

CMD [ "java", "-jar", "POC.jar" ]