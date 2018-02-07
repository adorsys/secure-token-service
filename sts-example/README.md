# sts-example

This is an example implementation of a secure-token-service with a fake login adapter included.

## Profiles 

By default this application will use an in-memory h2 database to store keystores.
Furthermore you can start this application with multiple spring profiles:

| Profile name | Type | Description |
|--------------|------|-------------|
| h2           | Database connector | Uses h2 (file system persistence) database connector |
| mysql        | Database connector | Uses mysql database connector |
| postgres     | Database connector | Uses postgres database connector |
| liquidbase   | Database migration | Uses liquidbase database migration |
| flyway       | Database migration | Uses flyway database migration |
| dev          | Settings           | Uses settings for easier debugging and development |

You can combine database-connector and database-migration profiles.
