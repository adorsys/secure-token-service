# StsClientExample

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 18.0.6.

## Development server

Run `npm run start` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Build

Run `npm run build` to build the project. The build artifacts will be stored in the `dist/` directory.
Run `npm run prod` to build the project's production artifacts. The build artifacts will be stored in the `dist/` directory.

## Running docker container

Run the container with following environment settings:

| Variable name | Description | Mandatory? | Example                                  |
|---------------|-------------|------------|------------------------------------------|
| NG_KEYCLOAK_AUTH_URL | The url to the keycloak auth endpoint | yes | http://keycloakhost:8180                 |
| NG_KEYCLOAK_REALM    | The keycloak realm name               | yes | moped                                    |
| NG_KEYCLOAK_CLIENT_ID | The keycloak realm client name       | yes | moped-client                             |
| NG_KEYCLOAK_SCOPE     | The scopes (=sts-audiences) which will be used for login. (comma-separated list) | yes | sts-service-component,other-service-component | 
| NG_SERVICE_URL        | The url of the service which will be used                                        | yes | http://servicehost:8887/helloworld       |

The web-service will listen to port `8080`.

Here is an example for `docker-compose.yml`:

```
version: '3.1'
services:
  sts-client:
    image: adorsys/sts-client-example:latest
    environment:
    - NG_KEYCLOAK_AUTH_URL: http://keycloakhost:8180
    - NG_KEYCLOAK_REALM: moped
    - NG_KEYCLOAK_CLIENT_ID: moped client
    - NG_KEYCLOAK_SCOPE: sts-service-component
    - NG_SERVICE_URL: http://servicehost:8887/helloworld
    - NG_SECRET_URL": "http://localhost:8887/secret
    ports:
    - 8090:8080
```
