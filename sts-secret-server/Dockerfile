FROM adorsys/java:17

WORKDIR /opt/
COPY ./target/sts-secret-server-exec.jar .

EXPOSE 8080

CMD exec $JAVA_HOME/bin/java $JAVA_OPTS -jar /opt/sts-secret-server-exec.jar
