FROM adorsys/java:8

WORKDIR /opt/
COPY ./target/sts-starter.jar .

EXPOSE 8888

CMD exec $JAVA_HOME/bin/java $JAVA_OPTS -jar /opt/sts-starter.jar
