FROM ubuntu:18.04

## Install packages
RUN apt-get update
RUN apt-get install -y git apache2 apache2-utils
RUN a2enmod env cgi alias rewrite

## Setup git root directory
ENV GIT_ROOT=/var/www/git
RUN mkdir -p ${GIT_ROOT}
RUN chown -Rfv www-data:www-data ${GIT_ROOT}
RUN touch ${GIT_ROOT}/.htpasswd

## Setup apache settings
COPY ./scripts/git.conf /etc/apache2/sites-available/git.conf
COPY ./scripts/git_create_repo.sh /usr/bin/mkrepo
RUN chmod +x /usr/bin/mkrepo

RUN a2dissite 000-default.conf
RUN a2ensite git.conf

## Create repo
ENV LATTE_REPO=${GIT_ROOT}/latte_repo
RUN mkrepo "latte_repo"
COPY ./test-scripts ${LATTE_REPO}
WORKDIR ${LATTE_REPO}
RUN git config user.email "<>"
RUN git config user.name "latte"
RUN git add .
RUN git commit -m "Initial commit"
RUN chown -Rf www-data:www-data ${LATTE_REPO}

## Run the server
VOLUME ${GIT_ROOT}

ENV APACHE_RUN_USER www-data
ENV APACHE_RUN_GROUP www-data
ENV APACHE_LOG_DIR /var/log/apache2
ENV APACHE_LOCK_DIR /var/lock/apache2
ENV APACHE_PID_FILE /var/run/apache2.pid
CMD /usr/sbin/apache2ctl -D FOREGROUND
EXPOSE 80/tcp