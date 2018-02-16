# Public Key Service

This interface helps exposing public keys of a network component.

## Intention

We can use the public key service to expose the public keys of a "Network Component". This network component can be:
  - An Identity Provider
  - An oAuth2 Client
  - An oAuth2 Resource Server
  - An OpenID Connect Relying Party 

In most scenarios, we will retrieve the public key of a target server to encrypt sensitive information that are designated to this server, but that are carried by a bearer token (see RFC 6749). A concrete example of such information is a "User Secret" that is needed by the target server to perform some operations on behalf of the end user. 

 
## How to use

### Dependencies

#### Maven

```
    <dependency>
        <groupId>de.adorsys.sts</groupId>
        <artifactId>sts-pop</artifactId>
        <version>${version}</version>
    </dependency>
```

#### Gradle

```
    dependencies {
        compile "de.adorsys.sts:sts-pop:${version}"
    }
```

### Configuration class

```
    @Configuration
    @EnablePOP
    public class YourConfigurationClass {
        ...
    }
```

## Endpoint

```
    $ curl -X GET --header 'Accept: application/json' 'http://${yourhost}/pop'
```

# Specifications

Library is used to implement following features:

- Proof-of-Possession Key Semantics for JSON Web Tokens (JWTs) 
  - https://tools.ietf.org/html/rfc7800
  - https://tools.ietf.org/html/draft-ietf-oauth-pop-architecture-08
   
- Implementation of the OpenID connect discovery of "Open Id Providers", "Open Id Clients" and "Relying Parties".
  - This interface provides a means for retrieving the jwks_uri of the target party (Json Web Keyset URI).

