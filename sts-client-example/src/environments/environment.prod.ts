export const environment = {
  production: true,
  keycloak: {
    url: window['sts__KEYCLOAK_AUTH_URL'] || 'https://sso-keycloak-playground.dev.adorsys.de/auth',
    realm: window['sts__KEYCLOAK_REALM'] || 'moped',
    clientId: window['sts__KEYCLOAK_CLIENT_ID'] || 'moped-client',
    /** scopes: comma-separated list of scopes*/
    scope: window['sts__KEYCLOAK_SCOPE'] || 'sts-service-component',
  },
  serviceUrl: window['sts__SERVICE_URL'] || 'https://service-component-keycloak-playground.dev.adorsys.de/helloworld'
};
