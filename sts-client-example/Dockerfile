FROM nginx:stable-alpine

COPY ./dist/ /usr/share/nginx/html/

COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY docker/entrypoint.sh /opt/entrypoint.sh

EXPOSE 8090

ENTRYPOINT /opt/entrypoint.sh
