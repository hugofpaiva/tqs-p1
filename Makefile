.PHONY: run-api
run-api: ## Run the Spring Boot server
	cd air-quality-service && ./mvnw spring-boot:run

.PHONY: test-api
test-api: ## Run the Spring Boot tests
	cd air-quality-service && mvn test

.PHONY: test
test: test-api ## Run tests 

.PHONY: dev
dev: run-api ## Start all services in development mode

.PHONY: prod
prod: ## Start all services containers in development mode
	 docker-compose build
	 docker-compose up -d
	

