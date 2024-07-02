# https://github.com/adorsys/dockerhub-pipeline-images/tree/master/nginx
# https://hub.docker.com/r/adorsys/nginx/
FROM adorsys/nginx:ubi

COPY ./dist/ .
COPY docker/create_env.json.sh /docker-entrypoint.d/
