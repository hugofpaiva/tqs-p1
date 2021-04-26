.PHONY: run-api
run-api: ## Run the Spring Boot server
	cd air-quality-service && mvn clean package && ./mvnw spring-boot:run

.PHONY: test
test: ## Run tests 
	cd air-quality-service && mvn test

.PHONY: dev
dev: run-api ## Start all services containers in development mode
	

