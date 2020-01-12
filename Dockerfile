FROM hypriot/rpi-java
MAINTAINER cbartram
USER root

WORKDIR /usr/build/watchr/

RUN /usr/bin/printf '\xfe\xed\xfe\xed\x00\x00\x00\x02\x00\x00\x00\x00\xe2\x68\x6e\x45\xfb\x43\xdf\xa4\xd9\x92\xdd\x41\xce\xb6\xb2\x1c\x63\x30\xd7\x92' > /etc/ssl/certs/java/cacerts
RUN update-ca-certificates -f
RUN /var/lib/dpkg/info/ca-certificates-java.postinst configure

EXPOSE 8080

ARG JAR_FILE=target/watchr-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} watchr.jar

ENTRYPOINT ["java", "-Djavax.net.debug=all", "-Djavax.net.ssl.trustStorePassword=changeit", "-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts", "-jar", "watchr.jar"]