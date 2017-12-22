# secure-token-service

This an implementation of the secure token service as specified in the token exchange working draft https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-08. 

This module does not implement oAuth2 authentication flows. The module focuses on the oAuth2 token production and management process. Most oAuth2 Server implement both authentication and token management.

Authentication is the process of identifying and legitimating the subject of authorization. Means the subject on behalf of which the oAuth token is produced. An authentication server does following:

- Authenticate the user
- Ask the token manager to produce the corresponding token
- Ask the token manager to invalidate the token

Token management solely focuses on managing oAuth tokens and exchanging them like specified in the token exchange working draft (https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-08)

## Build this solution

### Within docker

```
docker-compose --file build.docker-compose.yml up --build --remove-orphans 
```

## Run example application

```
docker-compose --file docker-compose.yml up --build --remove-orphans
```
