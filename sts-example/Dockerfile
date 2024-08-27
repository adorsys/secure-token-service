FROM adorsys/java:17

WORKDIR /opt/
COPY ./target/sts-example-exec.jar .

EXPOSE 8888

CMD exec $JAVA_HOME/bin/java $JAVA_OPTS -jar /opt/sts-example-exec.jar
