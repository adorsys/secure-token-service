FROM nginx:stable-alpine

COPY ./dist/ /usr/share/nginx/html/

COPY ./docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY docker/entrypoint.sh /opt/entrypoint.sh

# to make the container runable without root permissions
RUN ln -sf /dev/stdout /var/log/nginx/access.log \
    && ln -sf /dev/stderr /var/log/nginx/error.log \
    && chmod -R 777 /var/log/nginx /var/cache/nginx/ \
    && find /etc/nginx/ -type f | xargs chmod o+r \
    && find /etc/nginx/ -type d | xargs chmod o+rx \
    && sed -i 's%pid.*%pid /tmp/nginx.pid;%g' /etc/nginx/nginx.conf

EXPOSE 8090

ENTRYPOINT /opt/entrypoint.sh
