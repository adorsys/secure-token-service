Use this VM Argument to start sts as a java application.

Multibanking mock can be user as sample resource server.

```
-DRESOURCE_SERVER_NAMES=mbservice,mbmock -Dmbservice_AUDIENCE_NAME=mbservice -Dmbservice_ENDPOINT_URL=http://localhost:8081 -Dmbservice_JWKS_URL=http://localhost:8081/pop -Dmbservice_USER_SECRET_CLAIM=custom_secret -Dmbmock_AUDIENCE_NAME=http://localhost:10010 -Dmbmock_ENDPOINT_URL=http://localhost:10010 -Dmbmock_JWKS_URL=http://localhost:10010/pop -Dmbmock_USER_SECRET_CLAIM=mbmock_user_secret -DKEYSTORE_PASSWORD=assdfo32814
```