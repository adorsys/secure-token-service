# secret-server

Provides a standalone configurable secret-server instance.

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
    - audience: "moped-client"
      jwks-url: "http://your-client-service/pop"
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
