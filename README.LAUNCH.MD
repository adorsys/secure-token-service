Use this VM Argument to start sts as a java application.

Multibanking mock can be user as sample resource server.

```
-DRESOURCE_SERVER_NAMES=mbk_service,mbk_mock -Dmbk_service_AUDIENCE_NAME=http://localhost:8081 -Dmbk_service_ENDPOINT_URL=http://localhost:8081 -Dmbk_service_JWKS_URL=http://localhost:8081/pop -Dmbk_service_USER_SECRET_CLAIM=mbk_service_user_secret -Dmbk_mock_AUDIENCE_NAME=http://localhost:10010 -Dmbk_mock_ENDPOINT_URL=http://localhost:10010 -Dmbk_mock_JWKS_URL=http://localhost:10010/pop -Dmbk_mock_USER_SECRET_CLAIM=mbk_mock_user_secret -DKEYSTORE_PASSWORD=assdfo32814
```