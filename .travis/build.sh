#!/usr/bin/env bash
set -e

mvn --settings .travis/settings.xml clean package -DskipTests -B -V

docker run --rm -v `pwd`/sts-client-example:/opt/src adorsys/angular-cli:v1.6.4 npm install
docker run --rm -v `pwd`/sts-client-example:/opt/src adorsys/angular-cli:v1.6.4 npm run prod
