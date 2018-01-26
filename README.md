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
| `@EnableEncryption` | Enables the encryption service bean |
| `@EnableDecryption` | Enables the decryption service bean |
| `@EnableKeyRotation` | Enables the key-rotation for the key-management |
| `@EnableServerInfo` | Enables the server-info endpoint |
| `@EnableTokenAuthentication` | Enables token-authentication-service bean |

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
  resourceServers:
  - audience: <(text) the name of your resource server / the audience key>
    jwksUrl: <(text, url) the jwks-url of the resource-server, like "http://localhost:8888/pop">
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
      checkInterval: <(long) the time interval in milliseconds the key-rotation will check the keys>
      encKeyPairs:
        minKeys: <(integer) minimal count of stored encryption key-pairs>
        enabled: <(boolean) defines if the key-rotation is enabled for encryption key-pairs>
      signKeyPairs:
        minKeys: <(integer) minimal count of stored signature key-pairs>
        enabled: <(boolean) defines if the key-rotation is enabled for signature key-pairs>
      secretKeys:
        minKeys: <(integer) minimal count of stored secret keys>
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
      alias_prefix: <(text) the prefix of your generated key-aliases in this key-store>
      keys:
        encKeyPairs:
          initialCount: <(integer) initial count of generated encryption key-pairs>
          algo: <(text) the key-pair algorithm, like "RSA">
          sigAlgo: <(text) the key-pair signature algorithm, like "SHA256withRSA">
          size: <(integer) the key size, like 2048, 4096, ...>
          name: <(text) the string-representation of your key-pair>
          validityInterval: <(long) the interval in milliseconds the keys can be used for encryption>
          legacyInterval: <(long) the interval in milliseconds the keys can be used for decryption>
        signKeyPairs:
          initialCount: <(integer) initial count of generated signature key-pairs>
          algo: <(text) the key-pair algorithm, like "RSA">
          sigAlgo: <(text) the key-pair signature algorithm, like "SHA256withRSA">
          size: <(integer) the key size, like 2048, 4096, ...>
          name: <(text) the string-representation of your key-pair>
          validityInterval: <(long) the interval in milliseconds the keys can be used for signature creation>
          legacyInterval: <(long) the interval in milliseconds the keys can be used for signature check>
        secretKeys:
          initialCount: <(integer) initial count of generated secret-keys>
          algo: <(text) the key algorithm, like "AES">
          size: <(integer) the key size, like 128, 256, ...>
          validityInterval: <(long) the interval in milliseconds the keys can be used for encryption>
          legacyInterval: <(long) the interval in milliseconds the keys can be used for decryption>
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
    issUrl: <(text, url) the issuer-url of your identity-provider's token, like "https://your-idp-hostname/auth/realms/your-realm">
    jwksUrl: <(text, url) the jwks-endpoint url of your identity provider, like "https://your-idp-hostname/auth/realms/your-realm/protocol/openid-connect/certs">
    refreshIntervalSeconds: <(integer) --- unused >
```

## Build this solution

### Within docker

```
docker-compose --file build.docker-compose.yml up --build --remove-orphans 
```

## Run example application

```
docker-compose --file docker-compose.yml up --build --remove-orphans
```

| Container | URL |
|-----------|-----|
| keycloak  | http://localhost:8080 |
| STS       | http://localhost:8888 |
| Service Component | http://localhost:8887 |
| Angular client | http://localhost:8090 |
