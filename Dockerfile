FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY /target/userms-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
EXPOSE 8400
ENV JAVA_OPTS=""
RUN sh -c "touch userms-0.0.1-SNAPSHOT.jar"
ENTRYPOINT [ "java", "-jar", "userms-0.0.1-SNAPSHOT.jar" ]
