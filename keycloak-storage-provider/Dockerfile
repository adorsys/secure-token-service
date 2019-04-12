FROM jboss/keycloak:3.2.1.Final

ADD docker/cli/init_keycloak.cli /install/init_keycloak.cli

ADD target/keycloak-storage-provider.tar.gz /

RUN cd /install && /opt/jboss/keycloak/bin/jboss-cli.sh --file=/install/init_keycloak.cli

RUN rm -rf /opt/jboss/keycloak/standalone/configuration/standalone_xml_history
