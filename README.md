# Global Artisan Marketplace Backend

Spring Boot microservices backend for a handmade crafts marketplace. The repository contains five independent services, an API gateway, MongoDB-backed persistence, OpenAPI docs, Docker Compose for local orchestration, and a GitHub Actions workflow for Snyk dependency scanning.

## Services

| Service | Default app port | Docker Compose host port | Purpose |
|---|---:|---:|---|
| API Gateway | 8084 | 8084 | Single entry point and aggregated Swagger UI |
| User Service | 8080 | 8086 | Registration, login, JWT auth, profiles, user validation |
| Listing Service | 8081 | 8081 | Listing creation, catalog browse, search, stock checks |
| Order Service | 8082 | 8082 | Cart, checkout, buyer and seller order views |
| Review Service | 8083 | 8083 | Review creation and listing review queries |
| MongoDB | 27017 | 27017 | Local database for all services |

## Architecture

- `api-gateway` is a Spring Cloud Gateway app that routes `/api/auth/**`, `/api/users/**`, `/api/listings/**`, `/api/orders/**`, and `/api/reviews/**`.
- `user-service` owns authentication and user profiles. It is the only service with Spring Security and JWT dependencies.
- `listing-service`, `order-service`, and `review-service` are separate Spring Boot services backed by MongoDB.
- `order-service` integrates with `user-service` and `listing-service`.
- `review-service` integrates with `user-service`, `order-service`, and `listing-service`.
- OpenAPI is enabled on every service, and the gateway exposes a unified Swagger UI.

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Cloud Gateway 2023.0.2
- MongoDB
- Maven
- Docker Compose
- OpenAPI via `springdoc`
- Snyk GitHub Actions workflow for dependency scanning

## Repository Layout

```text
api-gateway/
user-service/
listing-service/
order-service/
review-service/
docs/
  api-contracts/
docker-compose.yml
```

Each service is its own Maven project. There is no root parent `pom.xml`.

## Running With Docker Compose

This is the fastest way to bring up the full backend locally.

### Required environment variables

- `SPRING_DATA_MONGODB_URI`
  Use this if you want to point services at MongoDB Atlas. If omitted, Compose uses the local `mongodb` container.
- `JWT_SECRET`
  Required for `user-service`. A development default exists, but you should override it.

### Start the stack

```bash
docker-compose up --build -d
```

### Main local URLs

- Gateway: `http://localhost:8084`
- Gateway Swagger UI: `http://localhost:8084/swagger-ui.html`
- User Service direct access in Compose: `http://localhost:8086`
- Listing Service direct access: `http://localhost:8081`
- Order Service direct access: `http://localhost:8082`
- Review Service direct access: `http://localhost:8083`

## Running Services Individually

Start MongoDB first, then run each service from its own folder.

```bash
cd user-service
mvn spring-boot:run
```

Repeat the same pattern for:

- `api-gateway`
- `user-service`
- `listing-service`
- `order-service`
- `review-service`

### Important local environment variables

#### Shared

- `SPRING_DATA_MONGODB_URI`

#### API Gateway

- `USER_SERVICE_URL`
- `LISTING_SERVICE_URL`
- `ORDER_SERVICE_URL`
- `REVIEW_SERVICE_URL`

#### User Service

- `JWT_SECRET`
- `JWT_EXPIRATION_MS`

#### Order Service

- `API_GATEWAY_URL`

#### Review Service

- `API_GATEWAY_URL`

#### Listing Service

- `API_GATEWAY_URL`

## API Surface

### User Service

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/users/{id}`
- `PUT /api/users/{id}`
- `GET /api/users/{id}/validate`

### Listing Service

- `POST /api/listings`
- `GET /api/listings/{id}`
- `GET /api/listings`
- `GET /api/listings/search`
- `GET /api/listings/category/{category}`
- `GET /api/listings/seller/{sellerId}`
- `POST /api/listings/stock/check`
- `POST /api/listings/stock/reduce`

### Order Service

- `POST /api/orders/cart`
- `POST /api/orders/checkout`
- `GET /api/orders`
- `GET /api/orders/seller/{sellerId}`
- `GET /api/orders/{id}`

The order endpoints use the `X-Buyer-Id` request header for buyer-scoped actions.

### Review Service

- `POST /api/reviews`
- `GET /api/reviews/listing/{listingId}`
- `GET /api/reviews/listings`
- `PATCH /api/reviews/{reviewId}/seller-reply`

## API Documentation

Each service exposes OpenAPI docs at `/v3/api-docs` and a local Swagger UI at `/swagger-ui.html`.

The gateway aggregates the service docs at:

- `http://localhost:8084/swagger-ui.html`

Static API contract files are also stored under `docs/api-contracts/`.

## Testing

Each service has its own test source set and can be tested independently.

Examples:

```bash
cd user-service
mvn test
```

```bash
cd api-gateway
mvn test
```

Several services include Testcontainers MongoDB test dependencies in their Maven configuration.

## Security Scanning

The repository includes a Snyk GitHub Actions workflow at `.github/workflows/snyk.yml`.

It scans:

- `api-gateway`
- `user-service`
- `listing-service`
- `order-service`
- `review-service`

Workflow behavior:

- Runs on pull requests
- Runs on pushes to `dev`, `main`, and `master`
- Uploads SARIF results to GitHub code scanning
- Runs `snyk monitor` on branch pushes

### Required GitHub secret

- `SNYK_TOKEN`

Create the token in your Snyk account, then add it in GitHub under repository `Settings` > `Secrets and variables` > `Actions`.

## Additional Docs

- Azure deployment notes: `docs/azure-backend-cicd.md`
- OpenAPI contracts: `docs/api-contracts/`
