FROM quay.io/keycloak/keycloak:22.0.4

COPY target/keycloak-storage-provider.jar /opt/keycloak/providers/

RUN /opt/keycloak/bin/kc.sh
