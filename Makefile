.PHONY: run-api
run-api: ## Run the Spring Boot server
	cd air-quality-service && ./mvnw spring-boot:run

.PHONY: run-web
run-web: ## Run the Angular Web Application
	cd web-application && npm install && npm run start

.PHONY: verify
verify: ## Run tests, integration tests and other major Maven lifecycles
	cd air-quality-service && mvn clean verify

.PHONY: dev
dev: run-api run-web ## Start all services in development mode

.PHONY: prod
prod: ## Start all services containers in production mode
	 docker-compose build
	 docker-compose up -d
	

