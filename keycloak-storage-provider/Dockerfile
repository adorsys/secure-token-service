FROM jboss/keycloak:5.0.0

COPY docker/root /

RUN /opt/jboss/keycloak/bin/jboss-cli.sh --file=/install.cli \
    && rm -rf /opt/jboss/keycloak/standalone/log/server* /opt/jboss/keycloak/standalone/configuration/standalone_xml_history

COPY target/keycloak-storage-provider.jar /opt/jboss/keycloak/modules/de/adorsys/sts/keycloak/storageprovider/main/
