#!/usr/bin/env bash
set -e

docker login -u $DOCKER_HUB_USER -p $DOCKER_HUB_PASSWORD

# ----- push sts-example -----

IMAGE_NAME="${STS_EXAMPLE_DOCKER_IMAGE_NAME}:${TRAVIS_TAG}"
LATEST_IMAGE_NAME="${STS_EXAMPLE_DOCKER_IMAGE_NAME}:latest"

docker build -t $IMAGE_NAME ./sts-example
docker tag $IMAGE_NAME $LATEST_IMAGE_NAME

docker push $IMAGE_NAME
docker push $LATEST_IMAGE_NAME

# ----- push sts-service-component-example -----

IMAGE_NAME="${STS_SERVICE_COMPONENT_EXAMPLE_DOCKER_IMAGE_NAME}:${TRAVIS_TAG}"
LATEST_IMAGE_NAME="${STS_SERVICE_COMPONENT_EXAMPLE_DOCKER_IMAGE_NAME}:latest"

docker build -t $IMAGE_NAME ./sts-service-component-example
docker tag $IMAGE_NAME $LATEST_IMAGE_NAME

docker push $IMAGE_NAME
docker push $LATEST_IMAGE_NAME
