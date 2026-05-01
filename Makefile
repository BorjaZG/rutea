dev:
	./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

prod:
	export $$(grep -v '^#' .env | xargs) && ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

build:
	./mvnw clean package -DskipTests

test:
	./mvnw test

db-up:
	docker compose -f docker-compose.dev.yaml up -d
	@echo "Esperando a que MariaDB esté lista..."
	@docker compose -f docker-compose.dev.yaml wait rutea-db

db-down:
	docker compose -f docker-compose.dev.yaml down

up:
	docker compose up -d

down:
	docker compose down

.PHONY: dev prod build test db-up db-down up down
