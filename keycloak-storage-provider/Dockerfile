FROM quay.io/keycloak/keycloak:25.0.2

COPY target/keycloak-storage-provider.jar /opt/keycloak/providers/

RUN /opt/keycloak/bin/kc.sh
