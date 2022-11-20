FROM java:8
VOLUME /tmp
COPY ./target/*.jar  /app.jar
RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]
