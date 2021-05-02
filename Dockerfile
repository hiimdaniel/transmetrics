FROM azul/zulu-openjdk-alpine:11.0.7-jre
ADD target/transmetrics.jar /app/transmetrics.jar
CMD [ \
  "java", \
  "-server", \
  "-jar", "/app/transmetrics.jar"]

