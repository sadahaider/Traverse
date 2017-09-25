FROM openjdk:8

ADD target/traverse-0.0.1-SNAPSHOT.war traverse.war

#EXPOSE 6379
EXPOSE 8080

ENTRYPOINT [ "java", "-jar", "traverse.war" ]