# secure-token-service

This an implementation of the secure token service as specified in the token exchange working draft https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-08. 

This module does not implement oAuth2 authentication flows. The module focuses on the oAuth2 token production and management process. Most oAuth2 Server implement both authentication and token management.

Authentication is the process of identifying and legitimating the subject of authorization. Means the subject on behalf of which the oAuth token is produced. An authentication server does following:

- Authenticate the user
- Ask the token manager to produce the corresponding token
- Ask the token manager to invalidate the token

Token management solely focuses on managing oAuth tokens and exchanging them like specified in the token exchange working draft (https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-08)

## Development

### Dependencies

You can easily use this libraries in your spring boot app via maven dependency:

```
    <dependency>
        <groupId>de.adorsys.sts</groupId>
        <artifactId>sts-spring</artifactId>
        <version>${version}</version>
    </dependency>
```

### Annotations

You can easily use features by adding following annotations to your spring `@Configuration` class:

| Annotation | Description |
|------------|-------------|
| `@EnablePOP` | Enables the Proof-Of-Possession endpoint |
| `@EnableResourceServerInitialization` | Enables the initialization of the resource server configuration read from the spring properties |
| `@EnableEncryption`                   | Enables the encryption service bean |
| `@EnableDecryption`                   | Enables the decryption service bean |
| `@EnableSecretDecryption`             | Enables the secret claim decryption service bean |
| `@EnableKeyRotation`                  | Enables the key-rotation for the key-management  |
| `@EnableServerInfo`                   | Enables the server-info endpoint                 |
| `@EnableTokenAuthentication`          | Enables token-authentication-service bean        |
| `@EnableSecurityContextSecretProviding` | Enables the security-context secret provider bean |
| `@EnableJacksonObjectMapping`           | Enables jackson object mapper SPI bean            |
| `@EnableSecretServer`                   | Enables the secret-server functionality in your application |
| `@EnableSecretServerClient`             | Enables the secret-server client bean                       |

## Features

### Proof-Of-Possession

Provides the public keys for encryption and signature check via the `/pop` endpoint.

Depends on:
* Key-Management

### Resource-Server-Configuration

Resource servers are used for encryption to manage the jwks-endpoints.

#### Configuration

You can configure the array of resources servers in your `application.yml`:

```
sts:
  resource-server-management:
    resource-retriever:
      http-connect-timeout: <http connect timeout for JWK set retrieval in milliseconds, default: 250>
      http-read-timeout: <http read timeout for JWK set retrieval in milliseconds, default: 250>
      http-size-limit: <http entity size limit for JWK set retrieval in bytes, default: 50 * 1024>
# cache settings
      cache:
        enabled: <(boolean, optional, default: false) defines if the secret-server client uses a internal cache for the secrets>
        maximum-size: <(integer, optional, default: 1000) defines the maximum cache size>
        expire-after-access: <(integer, optional, default: 10) defines the expiration time in minutes>
    resource-servers:
    - audience: <(text) the name of your resource server / the audience key>
      jwks-url: <(text, url) the jwks-url of the resource-server, like "http://localhost:8888/pop">
```

You have to decide:
* Which kind of `ResourceServerRepository` you will provide as Bean.

### Encryption

Provides the `EncryptionService` which can be used to encrypt sensitive data. The resource servers are used to get the public keys for encryption from their `/pop` endpoint.
The encrypted ciphertext is created by [Json Web Encryption](https://tools.ietf.org/search/rfc7516).

Depends on:
* Resource-Server-Configuration

### Decryption

Provides the `DecryptionService` which can be used to decrypt JWE-encrypted ciphertexts. The decryption-key has to be stored in local key-management, otherwise the decryption will fail.

Depends on:
* Key-Management

### Secret decryption

Usage of annotation `@EnableSecretDecryption` provides the `SecretDecryptionService` instance which is used to get the decrypted secret.
 The encrypted secret is provided by a bean implementing the `SecretProvider` interface.

### Secret provider

Provided by the `@EnableSecurityContextSecretProviding` annotation a bean instance of `SecurityContextSecretProvider` class is provided.
 This implementation reads the secret claims from the JWT token (stored in spring's security context).

#### Configuration

The `SecurityContextSecretProvider` needs the following configuration to work properly:

```
sts:
  secret-claim-property-key: <the claim property key used to read the right claim from the claims-set, mandatory>
  audience-name: <the application's audience name. Is used to read the right entry from the secrets map, mandatory)>
```

### Key-Management

Manages key-pairs for encryption/decryption and key-pairs to create or check signatures. Additionally secret-keys will be managed.

You have to decide:
* Which kind of `KeyStoreRepository` you will provide as Bean.

#### Key-rotation

You may enable the key-rotation feature by adding the `@EnableKeyRotation` annotation to your spring boot `@Configuration` class. Additionally you have to configure following properties:

```
sts:
  keymanagement:
    rotation:
      check-interval: <(long) the time interval in milliseconds the key-rotation will check the keys>
      enc-key-pairs:
        min-keys: <(integer) minimal count of stored encryption key-pairs>
        enabled: <(boolean) defines if the key-rotation is enabled for encryption key-pairs>
      sign-key-pairs:
        min-keys: <(integer) minimal count of stored signature key-pairs>
        enabled: <(boolean) defines if the key-rotation is enabled for signature key-pairs>
      secret-keys:
        min-keys: <(integer) minimal count of stored secret keys>
        enabled: <(boolean) defines if the key-rotation is enabled for secret-keys>
```

#### Key-generation

You have to configure the properties of the key-generation in your `application.yml`:

```
sts:
  keymanagement:
    keystore:
      password: <(text) the key-store password>
      type: <(text) the key-store type, like "UBER">
      name: <(text) the key-store name>
      alias-prefix: <(text) the prefix of your generated key-aliases in this key-store>
      keys:
        enc-key-pairs:
          initial-count: <(integer) initial count of generated encryption key-pairs>
          algo: <(text) the key-pair algorithm, like "RSA">
          sig-algo: <(text) the key-pair signature algorithm, like "SHA256withRSA">
          size: <(integer) the key size, like 2048, 4096, ...>
          name: <(text) the string-representation of your key-pair>
          validity-interval: <(long) the interval in milliseconds the keys can be used for encryption>
          legacy-interval: <(long) the interval in milliseconds the keys can be used for decryption>
        sign-key-pairs:
          initial-count: <(integer) initial count of generated signature key-pairs>
          algo: <(text) the key-pair algorithm, like "RSA">
          sig-algo: <(text) the key-pair signature algorithm, like "SHA256withRSA">
          size: <(integer) the key size, like 2048, 4096, ...>
          name: <(text) the string-representation of your key-pair>
          validity-interval: <(long) the interval in milliseconds the keys can be used for signature creation>
          legacy-interval: <(long) the interval in milliseconds the keys can be used for signature check>
        secret-keys:
          initial-count: <(integer) initial count of generated secret-keys>
          algo: <(text) the key algorithm, like "AES">
          size: <(integer) the key size, like 128, 256, ...>
          validity-interval: <(long) the interval in milliseconds the keys can be used for encryption>
          legacy-interval: <(long) the interval in milliseconds the keys can be used for decryption>
```

### Token authentication

Provides the `TokenAuthenticationService` Bean which extracts the `org.springframework.security.core.Authentication` from the Bearer token. The token has to be valid, otherwise this operation will return `null`.
The validation of the token will also be handled in this operation. Only authentication-servers provided by the auth-servers configuration will be accepted.
It's recommended to use this service in a request-filter (like the `JWTAuthenticationFilter` in this project).

Depends on:
* Auth-Servers configuration

#### Configuration

You have to configure the array of authservers in your `application.yml`, otherwise no Bearer-token will be accepted:

```
sts:
  authservers:
  - name: <(text) custom name of your identity provider>
    iss-url: <(text, url) the issuer-url of your identity-provider's token, like "https://your-idp-hostname/auth/realms/your-realm">
    jwks-url: <(text, url) the jwks-endpoint url of your identity provider, like "https://your-idp-hostname/auth/realms/your-realm/protocol/openid-connect/certs">
    refresh-interval-seconds: <(integer) --- unused >
```

### Secret-server and its client

Provides server and client functionality to store secrets in a separate database. Only registered clients are able to
 get the secrets via a token-exchange endpoint. The delivered token has to be valid and the client has to be known as a
 resource server, its pop-endpoint have to be accessible for the secret-server.

#### Server side

You can provide the secret-server functionality while using the `@EnableSecretServer` annotation in your spring configuration.
 This annotation enables the token-exchange endpoint with the needed services in the background. It needs a
 `SecretRepository` instance which is used to persist the secrets. For example you can store your secrets in a RDBMS
 like postgres or mysql if you use the additional annotation `@EnableJpaPersistence` in your configuration.

Alternatively you may deploy a ready-to-run secret-server provided by the *sts-secret-server* submodule and which
 you can run via docker: the image name is `adorsys/sts-secret-server` and can be found on
 [docker-hub](https://hub.docker.com/r/adorsys/sts-secret-server/).

##### Configuration

```
sts:
  secret-server:
    secret-length: <(integer, optional) secret length in bits (not the length of the base64 encoded string!) default: 256>
    endpoint: <(url as string, optional) the endpoint path of the secret-server's token-exchange endpoint. default: /secret-server/token-exchange >
    encryption:
      enabled: <(boolean, optional) defines if the secret encryption is enabled. default: false >
      algorithm: <(string, optional) defines the encryption algorithm. default: A256GCMKW >
      encryption-method: <(string, optional) defines the encryption method. default: A256GCM >
      key: <(string, optional) defines the encryption key in json format. default: none >
# You need to configure the same IDP instance(s) and its pop-entpoint(s) as authserver(s) as the clients are using to authenticate.
# Here is an example:
  authservers:
  - name: "local keycloak"
    iss-url : "http://localhost:8080/auth/realms/moped"
    jwks-url: "http://localhost:8080/auth/realms/moped/protocol/openid-connect/certs"

# You need to configure the clients as resource-servers with its pop-endpoints:
# Here is an example:
  resource-server-management:
    resource-servers:
    - audience: "moped-client"
      jwks-url: "http://my-moped-application:8080/pop"
```

#### Client side

On client-side you can communicate with a secret-server via the `SecretServerClient` bean. The interface is quite
 simple: there is a `String getSecret(String token)` method which delivers the decrypted and BASE64 encoded secret for
 the specified. Quite easier is the usage of the `SecretServerClientSecretProvider` bean. It provides the secret without
 the need to pass the token as parameter. Both beans are injectable after the usage of `@EnableSecretServerClient`
 annotation in your spring configuration.

##### Configuration

```
sts:
  secret-server-client:
    audience: <(string, mandatory, no default) The audience name of your application which is used to communicate with the secret-server>
    secret-server-uri: <(url as string, mandatory, no default) The url of the secret-server's token-exchange endpoint (Example: http://your-secret-server:8080/token/token-exchange)>
# cache settings:
    cache:
      enabled: <(boolean, optional, default: false) defines if the secret-server client uses a internal cache for the secrets>
      maximum-size: <(integer, optional, default: 1000) defines the maximum cache size>
      expire-after-access: <(integer, optional, default: 10) defines the expiration time in minutes>
```

## Build this solution

### Within docker

```
docker-compose --file build.docker-compose.yml down --remove-orphans && docker-compose --file build.docker-compose.yml up --build 
```

## Run example application

### with postgres database

```
docker-compose --file postgres.docker-compose.yml down --remove-orphans && docker-compose --file postgres.docker-compose.yml up --build
```

### with mysql 8 database

```
docker-compose --file mysql8.docker-compose.yml down --remove-orphans && docker-compose --file mysql8.docker-compose.yml up --build
```

### with mongo database

```
docker-compose --file mongo.docker-compose.yml down --remove-orphans && docker-compose --file mongo.docker-compose.yml up --build
```

| Container | URL |
|-----------|-----|
| keycloak  | http://localhost:8080 |
| STS       | http://localhost:8888 |
| Service Component | http://localhost:8887 |
| Angular client | http://localhost:8090 |
