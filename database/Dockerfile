FROM mysql:8.4

ENV MYSQL_ROOT_PASSWORD=root
ENV MYSQL_DATABASE=shopping_db

COPY init.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 3306