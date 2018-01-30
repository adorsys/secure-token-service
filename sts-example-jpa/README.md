# sts-example-jpa

## How to use with postgres

1. Add following dependency to `pom.xml`:

```
    <dependency>
        <groupId>de.adorsys.sts</groupId>
        <artifactId>sts-persistence-jpa-postgresql</artifactId>
    </dependency>
```

and build the application.

2. Run your postgres instance and adjust the datasource-properties in the `application-postgres.yml`.

3. Use the `postgres` spring profile with following command-line-argument:

```
--spring.profiles.active=postgres
```

## How to use with h2

1. Add following dependency to `pom.xml`:

```
    <dependency>
        <groupId>de.adorsys.sts</groupId>
        <artifactId>sts-persistence-jpa-h2</artifactId>
    </dependency>
```

2. Use the `h2` spring profile with following command-line-argument:

```
--spring.profiles.active=h2
```
