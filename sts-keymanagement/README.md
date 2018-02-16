# Key Management Service

This is java component built to generate and manage cryptographic session keys of network components.

## Features

### Plugable Key Store

The key management service provide a hoock for plugging in key storage modules.

Module must consume and produce a Java KeyStore object.

### Initial Generation of Keys

Following keys can be generated at startup 
- signatures key pairs 
- encryption key pairs
- encryption secret keys

### Built in Multi node Support

Some custom modules can provide built in multi node, thus providing a way to share the same key among multiple servers in the network.  

### Key rotation

The key rotation capability is necessary to:

- make sure life span of session keys of network components are limited,
- that private keys used by servers to decrypt "secret data" are destroyed after all derived credentials are expired.  

The availability time of a key is for example the max validity time of a bearer token. After a server rotates a key, it can hold old key for at most X-minutes to make sure all tokens issued an containing information to be decrypted using this key are expired.

