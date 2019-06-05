# secret-server

Provides a standalone configurable secret-server instance.

## Run the application

By default the secret-server is listening on port 8080 and is fully configurable via the [spring-boot mechanism](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).

### Java environment

You can run the spring-boot application via its `jar`-file easily:
```bash
$ java -jar sts-secret-server.jar
```

### Docker environment

You also can use the Docker-image pulled from Docker-Hub:

```bash
$ docker run adorsys/sts-secret-server:latest
```

## Configuration

```
sts:
  secret-server:
    secret-length: <(integer, optional) secret length in bits (not the length of the base64 encoded string!) default: 256, maximum: 8192>
    endpoint: <(url as string, optional) the endpoint path of the secret-server's token-exchange endpoint. default: /secret-server/token-exchange >
    encryption:
      enabled: <(boolean, optional) defines if the secret encryption is enabled. default: false >
      algorithm: <(string, optional) defines the encryption algorithm. default: A256GCMKW >
      encryption-method: <(string, optional) defines the encryption method. default: A256GCM >
      key: <(string, optional) defines the encryption key in json format. default: none >
```

### Encryption key

You can generate your own encryption-key using [Command line JSON Web Key (JWK) generator](https://connect2id.com/products/nimbus-jose-jwt/generator) or [JSON Web Key Generator (online)](https://mkjwk.org/).
Please note: secret-server is only supporting symmetric keys with a key-length of 128 bits (16 bytes), 192 bits (24 bytes) or 256 bits (32 bytes).

For example if you use the mkjwk online tool, you select 'Shared secret', specify the length with 256 and you will get the following key which is legal to use:

```
{
  "kty": "oct",
  "k": "S0bsqqJxRo-8QCWzQUlLTecj-OGQmBXfm8IdLdDg51c"
}
```

### Auth servers

In this section you have to configure your IDP the secret-server will accept the tokens from:

```
sts:
  authservers:
  - name: "local keycloak"
    iss-url : "http://your-idp/auth/realms/your-realm"
    jwks-url: "http://your-idp/auth/realms/your-realm/protocol/openid-connect/certs"

```

Make sure your secret-server is able to call the certs-endpoint of your IDP.

### Resource servers

In this section you have configure for which clients and services the secret-server will encrypt the secret containing in the exchanged token:

```
sts:
# You need to configure the clients as resource-servers with its pop-endpoints:
# Here is an example:
  resource-server-management:
    resource-servers:
      - audience: <(text) the name of your resource server / the audience key>
        jwks-url: <(text, url) the jwks-url of the resource-server, like "http://localhost:8888/pop">
    resource-retriever:
      http-connect-timeout: <http connect timeout for JWK set retrieval in milliseconds, default: 250>
      http-read-timeout: <http read timeout for JWK set retrieval in milliseconds, default: 250>
      http-size-limit: <http entity size limit for JWK set retrieval in bytes, default: 50 * 1024>
# cache settings
      cache:
        enabled: <(boolean, optional, default: false) defines if the secret-server client uses a internal cache for the secrets>
        maximum-size: <(integer, optional, default: 1000) defines the maximum cache size>
        expire-after-access: <(integer, optional, default: 10) defines the expiration time in minutes>
```

Please consider [Resource-Server-Configuration](https://github.com/adorsys/secure-token-service#resource-server-configuration) for further configuration.

### Key-Management

The secret-server has an own [Proof-Of-Possession](https://github.com/adorsys/secure-token-service#proof-of-possession) endpoint including key-management feature.
Following section describes the configuration for the key-management/keystore:

```
sts:
  keymanagement:
    keystore:
      password: <(text*) the key-store encryption password, mandatory>
      type: <(text) the key-store type, default: "UBER">
      name: <(text) the key-store name, default: "sts-secret-server-keystore">
      alias-prefix: <(text) the prefix of your generated key-aliases in this key-store, default: "sts-secret-server-">
      keys:
        enc-key-pairs:
          initial-count: <(integer) initial count of generated encryption key-pairs, default: 5>
          algo: <(text) the key-pair algorithm, default: "RSA">
          sig-algo: <(text) the key-pair signature algorithm, default: "SHA256withRSA">
          size: <(integer) the key size, default: 2048>
          name: <(text) the string-representation of your key-pair, default: Adorsys STS Secret Server>
          validity-interval: <(long) the interval in milliseconds the keys can be used for encryption, default: 3600000>
          legacy-interval: <(long) the interval in milliseconds the keys can be used for decryption, default: 86400000>
        sign-key-pairs:
          initial-count: <(integer) initial count of generated signature key-pairs, default: 5>
          algo: <(text) the key-pair algorithm, default: "RSA">
          sig-algo: <(text) the key-pair signature algorithm, default: "SHA256withRSA">
          size: <(integer) the key size, default: 2048>
          name: <(text) the string-representation of your key-pair, default: Adorsys STS Secret Server>
          validity-interval: <(long) the interval in milliseconds the keys can be used for signature creation, default: 3600000>
          legacy-interval: <(long) the interval in milliseconds the keys can be used for signature check, default: 86400000>
        secret-keys:
          initial-count: <(integer) initial count of generated secret-keys, default: 5>
          algo: <(text) the key algorithm, default "AES">
          size: <(integer) the key size, default: 256>
          validity-interval: <(long) the interval in milliseconds the keys can be used for encryption, default: 3600000>
          legacy-interval: <(long) the interval in milliseconds the keys can be used for decryption, default: 86400000>
```

The value for `sts.keymanagement.keystore.password` can be any string with minimum length of 1.

#### Key-Rotation

The following section describes the key-management/key-rotation configuration:

```
sts:
  keymanagement:
    rotation:
      check-interval: <(long) the time interval in milliseconds the key-rotation will check the keys, default: 60000>
      enc-key-pairs:
        min-keys: <(integer) minimal count of stored encryption key-pairs, default: 5>
        enabled: <(boolean) defines if the key-rotation is enabled for encryption key-pairs, default: true>
      sign-key-pairs:
        min-keys: <(integer) minimal count of stored signature key-pairs, default: 5>
        enabled: <(boolean) defines if the key-rotation is enabled for signature key-pairs, default: true>
      secret-keys:
        min-keys: <(integer) minimal count of stored secret keys, default: 5>
        enabled: <(boolean) defines if the key-rotation is enabled for secret-keys, default: true>
```

### Persistence

The secret server is able to store secrets in different types of databases:

| Database | Spring profile |
|----------|----------------|
| PostgreSQL | postgres     |
| MySQL      | mysql        |
| Mongo      | mongo        |
| H2         | default, h2  |

Please use the corresponding spring profile.
You also may consider the persistence documentation for [JPA](https://github.com/adorsys/secure-token-service/tree/master/sts-persistence-jpa#sts-persistence-jpa) and [mongo](https://github.com/adorsys/secure-token-service/tree/master/sts-persistence-mongo#sts-persistence-mongo) for further information.

### Available endpoints

| Endpoint | is adjustable via property | Description |
|----------|----------------------------|-------------|
| `/secret-server/token-exchange` | `sts.secret-server.endpoint` | The actual endpoint to get the secret via token-exchange |
| `/swagger-ui.html`              | | [Swagger UI](https://swagger.io/tools/swagger-ui/) to document and test endpoints |
| `/actuator`                     | | [Spring boot's actuator endpoints](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html) |
