.PHONY: build docker-build up down test clean-images run

DOCKER_IMAGE=dkb-url-shortener-app:latest

clean-images: down
	docker images -qa -f 'dangling=true' | xargs docker rmi
	docker image rm $(DOCKER_IMAGE) || (echo "Image $(DOCKER_IMAGE) didn't exist so not removed."; exit 0)

build:
	./gradlew clean build

docker-build: build clean-images
	docker build -t ${DOCKER_IMAGE} .

up: build docker-build
	docker-compose up -d

down:
	docker-compose down

test:
	./gradlew test

run:
	./gradlew bootRun


