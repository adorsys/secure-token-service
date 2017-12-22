FROM adorsys/openjdk-build-base:8

RUN mkdir -p /opt/src
WORKDIR /opt/src

COPY ./docker/build.cmd.sh /opt/build.cmd.sh

CMD ["/bin/sh","/opt/build.cmd.sh"]
