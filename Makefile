.PHONY: build up down

build:
	./gradlew build

up:
	docker-compose up -d

down:
	docker-compose down


