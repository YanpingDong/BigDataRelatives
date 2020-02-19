FROM ubuntu:18.04

RUN apt-get update && apt-get install -y nginx

# clear default nginx web
RUN cd /var/www/html && rm -rf *

# add index page 
ADD index.html /var/www/html/

EXPOSE 80

VOLUME [ "/var/www/html/" ]

# If you add a custom CMD in the Dockerfile, be sure to include -g daemon off; 
# in the CMD in order for nginx to stay in the foreground, 
# so that Docker can track the process properly 
# (otherwise your container will stop immediately after starting)!
CMD [ "/usr/sbin/nginx", "-g", "daemon off;" ] 
