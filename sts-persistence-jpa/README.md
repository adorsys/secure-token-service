# sts-persistence-jpa

## Preparation

1. You have to add following dependency to your `pom.yml`:

```
    <dependency>
        <groupId>de.adorsys.sts</groupId>
        <artifactId>sts-persistence-jpa</artifactId>
    </dependency>
```

2. To use the JPA-Repository to save your keystore, you have to add the `@EnableJpaPersistence` annotation to your spring configuration class.

## How to use with postgres

1. Add following dependency to your `pom.xml`:

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

## Database migration/refactoring

You can use flyway or liquibase for database-migration. Migration files for both ways are existing in this project.

### Use liquibase

The example module uses liquibase by default.
1. Add following dependency to your `pom.xml`:

```
    <dependency>
        <groupId>de.adorsys.sts</groupId>
        <artifactId>sts-persistence-jpa-liquibase</artifactId>
    </dependency>
```

Alternatively you can copy the migration scripts to your `resource`-folder. 

2. Adjust your `application.yml`:

```
liquibase:
  change-log: classpath:/db/migration/changelog.yml
```

Adjust the paths if you copied the migration files in your own project.

### Use flyway

1. Add following dependency to your `pom.xml`:

```
    <dependency>
        <groupId>de.adorsys.sts</groupId>
        <artifactId>sts-persistence-jpa-flyway</artifactId>
    </dependency>
```

Alternatively you can copy the migration scripts to your `resource`-folder. 

2. Adjust your `application.yml`. Make sure you use the correct migration files for your database type:

For h2:
```
flyway:
  locations:
  - db/migration/h2
```

For postgres:
```
flyway:
  locations:
  - db/migration/postgres
```
