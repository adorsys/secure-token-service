#!/usr/bin/env bash
set -e

DOCKER_BUILD_PATH=${PWD}/sts-client-example

chmod 777 -R ${DOCKER_BUILD_PATH}

docker run \
  --rm \
  -v ${DOCKER_BUILD_PATH}:/opt/src \
  --entrypoint="" \
  -w /opt/src \
  adorsys/ci-build:latest \
  /opt/src/docker/build.cmd.bash
