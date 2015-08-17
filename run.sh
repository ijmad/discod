#!/bin/bash

RES_ID=$(docker run -d \
 -e "DOCKER_HOST=$DOCKER_HOST" \
 -e "DOCKER_TLS_VERIFY=$DOCKER_TLS_VERIFY" \
 -e "DOCKER_CERT_PATH=/cert" \
 -v $DOCKER_CERT_PATH:/cert \
 -v /var/run/docker.sock:/var/run/docker.sock \
 discod/service-resolver:latest)

DNS_ID=$(docker run -d \
  --link $RES_ID:sr \
  discod/dns:latest)

echo "Use --dns=$(docker inspect -f='{{.NetworkSettings.IPAddress}}' $DNS_ID)"