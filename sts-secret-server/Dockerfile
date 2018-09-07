FROM adorsys/java:8

WORKDIR /opt/
COPY ./target/sts-secret-server.jar .

EXPOSE 8080

CMD exec $JAVA_HOME/bin/java $JAVA_OPTS -jar /opt/sts-secret-server.jar
