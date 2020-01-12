FROM hypriot/rpi-java

# Add a volume pointing to /tmp
VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/watchr-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} watchr.jar

ENTRYPOINT ["java", "-jar", "watchr.jar"]