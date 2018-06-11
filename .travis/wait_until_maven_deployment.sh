#!/bin/bash

GITHUB_PROJECT=adorsys/secure-token-service
MAVEN_CENTRAL_PROJECT=de/adorsys/sts/secure-token-service
MAVEN_ARTIFACT=secure-token-service
TIMEOUT=3600
INTERVAL=60

LATEST_VERSION=`git ls-remote https://github.com/${GITHUB_PROJECT} | grep refs/tags | grep -oP [0-9]+\.[0-9]+\.[0-9]+$ | tail -n 1`
URL=http://repo1.maven.org/maven2/${MAVEN_CENTRAL_PROJECT}/${LATEST_VERSION}/${MAVEN_ARTIFACT}-${LATEST_VERSION}.pom

echo 'wait until maven artifact is available...'
./.travis/wtfc.sh -T ${TIMEOUT} -S 0 -I ${INTERVAL} curl -f ${URL}
