# DKB Code Factory - URL Shortener

## Prerequisites

* The service is dependent on a Redis instance. The properties for the instance are specified in [application resources](src/main/resources).
* The service needs to specify the URL that it'll use in the application properties, based on profiles (see below). This is the public root URL that (at least for the prod) will be accessible externally, and it will be used as a base URL to produce a shortened URL.

## Getting started

There is a [Makefile](Makefile) with several commands to streamline working with the application. In the simplest case, one needs just the following two commands.

### Running the service and Redis in docker

```shell
make up
```

### Stopping the service and Redis in docker

```shell
make down
```

The `make up` and `make down` provide a running application with a Redis instance. The docker version of the application uses port 8081 (as opposed to the "normal" `server.port` that's used by the `bootRun` gradle task).

```text
CONTAINER ID   IMAGE                          COMMAND                  CREATED         STATUS         PORTS                    NAMES
eb0649bc8fde   dkb-url-shortener-app:latest   "java -jar -Dspring.…"   7 seconds ago   Up 6 seconds   0.0.0.0:8081->8081/tcp   dkb-url-shortener-app-1
29d9c6a0e47e   redis:alpine                   "docker-entrypoint.s…"   7 seconds ago   Up 6 seconds   0.0.0.0:6379->6379/tcp   dkb-url-shortener-redis-1
```

These commands are dependent on other commands that can also be run separately if needed. 

### Cleaning the docker image

```shell
make clean-images
```

### Building the application

```shell
make build
```

### Running the tests

```shell
make test
```

### Building the image

```shell
make docker-build
```

### Running the SpringBoot app

```shell
make run
```

## API description

This is a simple MVP of a URL shortener service. It has only two endpoints:

1. `POST` to the service root URL takes a URL string in request body and returns JSON containing the long (provided) URL and a shortened URL:

```http request
POST http://localhost:8080/
Content-Type: text/plain
Accept: application/json

https://www.example.com?param=2
```

Example response:

```json
{
  "longUrl": "https://www.example.com?param=2",
  "shortUrl": "http://localhost:8080/31l0"
}
```

2. `GET` to the service root URL takes a path parameter and also returns JSON containing the long (resolved) URL and a shortened (provided) URL:

```http request
GET http://localhost:8080/31l0
Accept: application/json
```

It would return the same response, but in this response, the long URL would be a resolved one and the short would be just taken from a parameter.

**NOTE: the service does no redirect to the long URL. It just provides it.**

## Application profiles

There are several profiles:

* default - the configuration is specified in the [application.yaml](src/main/resources/application.yaml) file.
* `docker` - the configuration is specified in the [application-docker.yaml](src/main/resources/application-docker.yaml) file. This profile is used to start everything (the application itself and the Redis instance that's required to run it) in Docker.
* `prod` - the configuration is specified in the [application-prod.yaml](src/main/resources/application-prod.yaml) file. The file that should be used to override the properties for a production instance.

## Q&A

* Why is it not reactive?

I don't see a need for a reactive application as an MVP. It's not too difficult to rewrite it if need be, however, for the MVP - or as an exercise - I see the reactive programming as adding extra complexity where it's not needed. The Spring MVC code is way simpler and therefore more readable.

* Why Redis?

Redis is a blazingly fast key-value storage solution that can store basically anything. It also has a nice bonus of being able to generate a unique ID (just like an autoincrement key). This ID can be used for transforming into the hash part of the short URL that we require.

* But Redis is an in-memory storage!

Yes, but:
1. You can set up replication. The replicas will hold a copy of a master data and will serve as a failover. So if the master instance fails, the replicas remain.
2. You can set up [Redis persistence](https://redis.io/docs/management/persistence/). In this way, the data can be stored on disk as snapshots, or as a log files that can be replayed. For the docker containers, it is possible to mount a shared volume to use as persistent storage.

* How do you generate the alphanumeric ID from the incremented one?

In a very stupid way, by using the [Long.toString(radix: Int)](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/to-string.html) with a radix of 32. This gives us letters, not just numbers, since the radix is big enough.

* So if it's a `Long`, what happens when it overflows?

`Long.MAX_VALUE` is `9223372036854775807`. It's 9 quadrillion. The [world population](https://en.wikipedia.org/wiki/World_population) is about 8 billion people now. So even if every person on earth would be adding a URL into our shortener, we'd still have a whole lot to spare. At this point, we'd probably have much more serious concerns about how to scale the storage itself.   

## Possible improvements

* Protect from possible attacks by checking the URLs against the regexp. For now, there are no checks on the incoming data.
* Convert the application into a reactive one (when premature optimization stops being premature).
* Add the OpenAPI descriptions to the REST endpoints.
* Have an option to add TTL to the links and clean up the ones that have expired.
* As an alternative, keep the "last accessed" attribute on the links and clean them up after some fixed expiration period when unused.
* Add registration and some kind of bonuses for registered (and subscribed) users (some fancy URLs).









