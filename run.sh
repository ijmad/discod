#!/bin/bash

DISCOD_SERVICES_ID=$(docker run -d \
 -e "DOCKER_HOST=$DOCKER_HOST" \
 -e "DOCKER_TLS_VERIFY=$DOCKER_TLS_VERIFY" \
 -e "DOCKER_CERT_PATH=/cert" \
 -v $DOCKER_CERT_PATH:/cert \
 discod-services:latest)

# --link db:db

docker inspect -f='{{.NetworkSettings.IPAddress}}' $DISCOD_SERVICES_ID