#!/bin/bash
SR_ADDR=$(echo $SR_PORT | sed s/^tcp:\\/\\/// | sed s/:/#/)
dnsmasq --no-daemon --server=/container/$SR_ADDR --server=/image/$SR_ADDR