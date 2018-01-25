FROM adorsys/angular-cli:v1.6.5

COPY ./docker/build.cmd.bash /opt/build.cmd.bash

RUN mkdir -p /opt/src
WORKDIR /opt/src

CMD ["/bin/sh", "/opt/build.cmd.bash"]
