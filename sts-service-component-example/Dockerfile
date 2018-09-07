FROM adorsys/java:8

WORKDIR /opt/
COPY ./target/sts-service-component-example.jar .

EXPOSE 8887

CMD exec $JAVA_HOME/bin/java $JAVA_OPTS -jar /opt/sts-service-component-example.jar
