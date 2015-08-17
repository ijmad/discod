#!/bin/bash
mvn package && docker build -t discod/service-resolver .