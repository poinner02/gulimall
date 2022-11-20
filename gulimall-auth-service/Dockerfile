FROM java:8
VOLUME /tmp
COPY ./target/gulimall-auth-service-0.0.1-SNAPSHOT.jar  /app.jar
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod"]
