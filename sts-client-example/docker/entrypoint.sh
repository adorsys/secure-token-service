#!/bin/sh

touch /tmp/environment_variables.conf

if [ ! -z "$KEYCLOAK_AUTH_URL" ]; then
  echo "sub_filter '<script>window.sts__KEYCLOAK_AUTH_URL = \"__KEYCLOAK_AUTH_URL__\";</script>'  '<script>window.sts__KEYCLOAK_AUTH_URL = \"${KEYCLOAK_AUTH_URL}\";</script>';" >> /tmp/environment_variables.conf;
fi

if [ ! -z "$KEYCLOAK_REALM" ]; then
  echo "sub_filter '<script>window.sts__KEYCLOAK_REALM = \"__KEYCLOAK_REALM__\";</script>'  '<script>window.sts__KEYCLOAK_REALM = \"${KEYCLOAK_REALM}\";</script>';" >> /tmp/environment_variables.conf;
fi

if [ ! -z "$KEYCLOAK_CLIENT_ID" ]; then
  echo "sub_filter '<script>window.sts__KEYCLOAK_CLIENT_ID = \"__KEYCLOAK_CLIENT_ID__\";</script>'  '<script>window.sts__KEYCLOAK_CLIENT_ID = \"${KEYCLOAK_CLIENT_ID}\";</script>';" >> /tmp/environment_variables.conf;
fi

if [ ! -z "$KEYCLOAK_SCOPE" ]; then
  echo "sub_filter '<script>window.sts__KEYCLOAK_SCOPE = \"__KEYCLOAK_SCOPE__\";</script>'  '<script>window.sts__KEYCLOAK_SCOPE = \"${KEYCLOAK_SCOPE}\";</script>';" >> /tmp/environment_variables.conf;
fi

if [ ! -z "$SERVICE_URL" ]; then
  echo "sub_filter '<script>window.sts__SERVICE_URL = \"__SERVICE_URL__\";</script>'  '<script>window.sts__SERVICE_URL = \"${SERVICE_URL}\";</script>';" >> /tmp/environment_variables.conf;
fi

nginx -g "daemon off;"
