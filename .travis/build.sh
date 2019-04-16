#!/usr/bin/env bash
set -e

mvn --settings .travis/settings.xml clean verify -B -V

.travis/build_client.sh
