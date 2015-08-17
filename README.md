# discod
Lightweight DNS-based service discovery for docker-based development environments

## What is this thing

It provides a DNS server that will resolve addresses in the usual way, but will also resolve anything with a `.service` domain to the current set of running docker images, either by its container ID (e.g. `b0466da37f60.service`) or name (e.g. `nostalgic_turing.service`).

## How to use it

You don't need the whole shebang unless you want to send me a pull request. To run it, you only need `run.sh` because the images are published in DockerHub, so should download automatically. 

1. Start the two components using `run.sh`.
2. Use the outputted DNS server as a --dns option to any further containers you start.
3. You can then reference any other containers using the .service syntax above.

For example:

```
docker run -ti --dns=172.17.0.6 ubuntu /bin/bash
```

## Why?

Docker provides a linking mechanism that achieves similar things, but you can't restart containers without breaking links. This is a problem when you're building things with docker-compose, because a whole tree of links is then broken by container restarts. If you use this instead, restarting a container is easy.