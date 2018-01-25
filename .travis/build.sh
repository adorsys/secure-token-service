#!/usr/bin/env bash
set -e

mvn --settings .travis/settings.xml clean package -DskipTests -B -V

.travis/build_client.sh
