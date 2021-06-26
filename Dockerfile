FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR application
RUN addgroup -S supportyoursport && adduser -S supportyoursport -G supportyoursport
USER supportyoursport:supportyoursport

COPY target/*-jar-with-dependencies.jar ./supportyoursport.jar

ENTRYPOINT ["java", "-jar", "supportyoursport.jar"]
