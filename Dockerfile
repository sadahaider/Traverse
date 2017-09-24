FROM openjdk:8

ADD target/traverse-0.0.1-SNAPSHOT.war traverse.war

#EXPOSE 6379 #redis
EXPOSE 8080 #server

ENTRYPOINT["java", "-jar", "traverse.war"]