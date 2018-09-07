# https://github.com/adorsys/dockerhub-pipeline-images/tree/master/nginx
# https://hub.docker.com/r/adorsys/nginx/
FROM adorsys/nginx

COPY ./dist/ /opt/app-root/src/
COPY ./docker/root /

ENTRYPOINT ["/entrypoint.sh"]

CMD ["/usr/libexec/s2i/run"]
