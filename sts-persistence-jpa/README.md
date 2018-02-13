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

## Default database

By default this package will provide an embedded h2-database you do not need to configure.

## How to use with postgres

1. Make sure your postgres-instance is running and adjust the datasource-properties in the `application-postgres.yml`.

2. Use the `postgres` spring profile with following command-line-argument:

```
--spring.profiles.active=postgres
```

## How to use with mysql 5.7 and 8.0

1. Make sure your mysql-instance is running and adjust the datasource-properties in the `application-postgres.yml`.

2. Use the `mysql` spring profile with following command-line-argument:

```
--spring.profiles.active=mysql
```

## How to use with a persistent h2

1. Use the `h2` spring profile with following command-line-argument:

```
--spring.profiles.active=h2
```

Your h2-database files will be stored as `sts_h2_db.mv.db` and `sts_h2_db.trace.db` as default.

## Database migration/refactoring

You can use flyway or liquibase for database-migration. Migration files for both ways are existing in this project.

### Default

liquibase will be used as default migration tool.

### Use liquibase (explicitly)

1. Use the `liquibase` spring profile with following command-line-argument:

```
--spring.profiles.active=liquibase
```

Alternatively you can copy the migration scripts to your `resource`-folder. 
Do not forget to adjust the paths to your migration scripts in your `application.yml`:

```
liquibase:
  change-log: classpath:/db/migration/changelog.yml
```

### Use flyway

1. Use the `flyway` spring profile with following command-line-argument:

```
--spring.profiles.active=flyway
```

Like for liquibase you can copy the migration scripts to your `resource`-folder. 
You need to adjust your `application.yml`. Make sure you use the correct migration files for your database type:

For h2:
```
flyway:
  locations:
  - db/migration/flyway/h2
```

For postgres:
```
flyway:
  locations:
  - db/migration/flyway/postgres
```

For mysql:
```
flyway:
  locations:
  - db/migration/flyway/mysql
```
