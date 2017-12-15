// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  keycloak: {
    url: 'https://sso-keycloak-playground.dev.adorsys.de/auth',
    realm: 'moped',
    clientId: 'moped-client',
    /** scopes: comma-separated list of scopes*/
    scope: 'sts-service-component'
  },
  serviceUrl: 'https://service-component-keycloak-playground.dev.adorsys.de/helloworld'
};
