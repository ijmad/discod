# discod
Lightweight DNS-based service discovery for docker-based development environments. An improvement on the functionality built in to docker because it's *live*, i.e. it doesn't need machines to pick up changes to `/etc/hosts` or `/etc/resolv.conf` or be restarted, because this is being done inside the DNS server.

The DNS server that will resolve addresses in the usual way, but will also resolve anything with a `.container` domain to the current set of running docker containers, either by its container ID (e.g. `b0466da37f60.container`) or name (e.g. `nostalgic_turing.container`), returning their internal network address (usually in the `172.17.x.x` range).

Additionally, any request to a `.image` domain will be resolved by looking at the current set of running containers and the images used to run them, and will balance any requests across running instances of the image. For example, `ubuntu.image` or `microservice.image`. This also lets you bind more loosely in various scenarios than addressing containers specifically which can be a little unpredictable.

Running docker containers are able to communicate with eachother freely once they have an IP addresse to any ports they have exposed.

Docker adds `.bridge` addresses automatically, but because this is operating via DNS, with a minimum TTL, the requests should be fresh and up-to-date all the time, as long as consumers respect DNS caching, rather than having to wait for changes in `/etc/hosts` to be picked up

This is most certainly not production-quality.
Please feel free to send me pull requests.

## How to use it

You don't need to checkout whole shebang unless you want to send me a pull request. To run it, you only need `discod-start.sh` because the images are published in `DockerHub`, so should download automatically. 

So try this:

```
curl -L https://raw.githubusercontent.com/ijmad/discod/master/discod-start.sh > ./discod-start.sh
chmod +x ./discod-start.sh
```

1. Start discod by issuing the command `./discod-start.sh`.
2. This in turn starts two docker containers.
3. Use the outputted DNS server as a `--dns` option to any further containers you start.

Inside a container, you can then reference any other container using the `.service` syntax above. By default, container names are not predictable, but you can make them so by always doing `docker run --name foo <container>`. Things like `docker-compose` name containers fairly consistently anyway, although I may eventually add support for finding containers by image name too.

For example:

```
docker run -ti --dns=172.17.0.6 ubuntu /bin/bash
```

## Why?

Docker provides a linking mechanism that achieves similar things, but you can't restart containers without breaking links. This is a problem when you're building things with docker-compose, because a whole tree of links is then broken by container restarts. If you use this instead, restarting a container is easier as links will be established again after as long as you use a consistent name.

## Quick note on inner workings

The service resolver does a trick to enable it to access the host machine's Docker via its remote API. This involves forwarding environment variables and mounting volumes. `discod-start.sh` should be able to figure out how to talk to docker, but this has not been exhaustively tested.
