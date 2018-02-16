# sts-persistence-mongo

## Preparation

1. You have to add following dependency to your `pom.yml`:

```
    <dependency>
        <groupId>de.adorsys.sts</groupId>
        <artifactId>sts-persistence-mongo</artifactId>
        <version>${secure-token-service.version}</version>
    </dependency>
```

2. To use the Mongo-Repository to save your keystore, you have to add the `@EnableMongoPersistence` annotation to your spring configuration class.

3. Use the `mongo` spring profile with following command-line-argument:

```
--spring.profiles.active=mongo
```

Note: Spring provides alternative ways to set profiles.

4. Setup mongo connection configuration:

```
spring:
  data:
    mongodb:
      host: <your mongo host - default: localhost>
      port: <your mongo port -  default: 27017>
      username: <your mongo username -  default: <empty>>
      password: <your mongo password -  default: <empty>>
      database: <your mongo database name -  default: sts>
```
