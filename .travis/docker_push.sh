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

# ----- push sts-client-example -----

IMAGE_NAME="${STS_CLIENT_EXAMPLE_DOCKER_IMAGE_NAME}:${TRAVIS_TAG}"
LATEST_IMAGE_NAME="${STS_CLIENT_EXAMPLE_DOCKER_IMAGE_NAME}:latest"

docker build -t $IMAGE_NAME ./sts-client-example
docker tag $IMAGE_NAME $LATEST_IMAGE_NAME

docker push $IMAGE_NAME
docker push $LATEST_IMAGE_NAME

# ----- push sts-secret-server -----

IMAGE_NAME="${STS_SECRET_SERVER_DOCKER_IMAGE_NAME}:${TRAVIS_TAG}"
LATEST_IMAGE_NAME="${STS_SECRET_SERVER_DOCKER_IMAGE_NAME}:latest"

docker build -t $IMAGE_NAME ./sts-secret-server
docker tag $IMAGE_NAME $LATEST_IMAGE_NAME

docker push $IMAGE_NAME
docker push $LATEST_IMAGE_NAME

# ----- push keycloak-storage-provider -----

IMAGE_NAME="${STS_KEYCLOAK_ADAPTER_DOCKER_IMAGE_NAME}:${TRAVIS_TAG}"
LATEST_IMAGE_NAME="${STS_KEYCLOAK_ADAPTER_DOCKER_IMAGE_NAME}:latest"

docker build -t $IMAGE_NAME ./keycloak-storage-provider
docker tag $IMAGE_NAME $LATEST_IMAGE_NAME

docker push $IMAGE_NAME
docker push $LATEST_IMAGE_NAME
