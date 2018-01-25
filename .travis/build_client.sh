#!/usr/bin/env bash
set -e

IMAGE_NAME=local/build__sts-client-example:latest
DOCKER_BUILD_PATH=`pwd`/sts-client-example

docker build --file ${DOCKER_BUILD_PATH}/build.Dockerfile -t ${IMAGE_NAME} ${DOCKER_BUILD_PATH}
docker run --rm -v ${DOCKER_BUILD_PATH}:/opt/src ${IMAGE_NAME}
