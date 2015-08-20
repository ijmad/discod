#!/bin/bash

# do our darndest to find some working docker!
if [ -e "/var/run/docker.sock" ]; then
  OPTIONS="-v /var/run/docker.sock:/var/run/docker.sock"
elif [ ! -z "$DOCKER_HOST" ] && [ ! -z "$DOCKER_CERT_PATH" ] && [ -d "$DOCKER_CERT_PATH" ]; then
  OPTIONS="-e \"DOCKER_HOST=$DOCKER_HOST\" -e \"DOCKER_TLS_VERIFY=$DOCKER_TLS_VERIFY\" -e \"DOCKER_CERT_PATH=/cert\" -v $DOCKER_CERT_PATH:/cert"
else
  B2D=$(which boot2docker)
  if [ ! -x "$B2D" ] ; then
    B2D=/usr/local/bin/boot2docker
  fi
  
  if [ -x "$B2D" ] ; then
    $(boot2docker shellinit)
  fi
  
  if [ ! -z "$DOCKER_HOST" ] && [ ! -z "$DOCKER_CERT_PATH" ] && [ -d "$DOCKER_CERT_PATH" ]; then
    OPTIONS="-e \"DOCKER_HOST=$DOCKER_HOST\" -e \"DOCKER_TLS_VERIFY=$DOCKER_TLS_VERIFY\" -e \"DOCKER_CERT_PATH=/cert\" -v $DOCKER_CERT_PATH:/cert"
  else
    echo "Could not find working docker UNIX or TCP socket"
    exit 1
  fi
fi

# run the two parts of discod
RES_ID=$(docker run -d $OPTIONS discod/service-resolver)
DNS_ID=$(docker run -d --link $RES_ID:sr discod/dns)

# print the result!
echo "Use --dns=$(docker inspect -f='{{.NetworkSettings.IPAddress}}' $DNS_ID)"
