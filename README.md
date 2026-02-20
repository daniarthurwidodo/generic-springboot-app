# Enterprise Spring Boot 3 Backend Boilerplate

A reusable, enterprise-grade backend template built with **Spring Boot 3** and **Java 21**, following Clean Architecture principles. Supports modular monolith and microservices transition.

## Features

### Authentication & Security
- JWT Authentication with refresh tokens
- OAuth2 support (Google, GitHub)
- Role-Based Access Control (RBAC)
- Public endpoint support
- OWASP-compliant security

### Database & Storage
- **PostgreSQL** - Primary RDBMS
- **MongoDB** - Document database
- **Flyway** - Database migrations

### Messaging & Caching
- **Apache Kafka** - Event-driven architecture
- **Redis** - Caching and distributed locking

### API Design
- RESTful APIs with URI versioning (`/api/v1/...`)
- Standardized response format
- Target: <200ms average latency

## Tech Stack

| Component | Technology |
|-----------|------------|
| Runtime | Java 21 |
| Framework | Spring Boot 3 |
| Security | Spring Security, JWT, OAuth2 |
| Databases | PostgreSQL, MongoDB |
| Cache | Redis |
| Messaging | Apache Kafka |
| Migrations | Flyway |
| Testing | JUnit 5, Mockito, MockMvc |
| Deployment | Docker, Docker Compose |

## Architecture

Clean Architecture with four layers:

```
com.company.project
├── presentation      # Controllers, DTOs
├── application       # Use cases, services
├── domain           # Entities, business logic, interfaces
└── infrastructure   # DB, Kafka, Redis, Security implementations
```

**Dependency Rule:** Outer layers depend on inner layers only.

## Getting Started

### Prerequisites
- Java 21+
- Podman & Podman Compose (or Docker & Docker Compose)

### 1. Start Infrastructure (Databases, Kafka, Redis)

```bash
podman compose up -d
```

This starts infrastructure services:
- `postgres` - PostgreSQL database (port 5432)
- `mongodb` - MongoDB database (port 27017)
- `redis` - Redis cache (port 6379)
- `kafka` - Kafka broker (port 9092)
- `zookeeper` - Kafka coordination (port 2181)

### 2. Run Spring Boot Application Locally

```bash
./mvnw spring-boot:run
```

Or build and run the JAR:

```bash
./mvnw clean package -DskipTests
java -jar target/*.jar
```

The app will be available at `http://localhost:8080`.

### Useful Commands

```bash
# View running containers
podman compose ps

# View logs
podman compose logs -f

# Stop all services
podman compose down

# Stop and remove volumes
podman compose down -v
```

### API Response Format

All endpoints return standardized responses:

```json
{
  "timestamp": "2026-02-20T12:00:00Z",
  "status": 200,
  "data": {},
  "error": null
}
```

## API Documentation

### API Specification

The API is documented using OpenAPI 3.0 specification.

**Files:**
- **OpenAPI Spec (YAML)**: [`src/main/resources/api/openapi-spec.yaml`](src/main/resources/api/openapi-spec.yaml)
- **Insomnia Collection (JSON)**: [`insomnia-collection.json`](insomnia-collection.json)

### Import into Insomnia

**Recommended: Import JSON Collection**
1. Open Insomnia
2. Click **Import Data** (or `Ctrl/Cmd + I`)
3. Select **OpenAPI 3.0** or **Swagger 2.0** format
4. Choose `insomnia-collection.json`
5. Click **Import**

**Alternative: Import YAML Spec from Resources**
1. Open Insomnia
2. Click **Import Data**
3. Select **OpenAPI 3.0** format
4. Choose `src/main/resources/api/openapi-spec.yaml`
5. Click **Import**

> **Note:** Import the JSON file for best results. The YAML file in `src/main/resources/` is meant for Spring Doc integration.

### Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Health check |
| GET | `/hello` | Hello endpoint |
| POST | `/api/v1/sql/todo` | Create todo |
| GET | `/api/v1/sql/todo` | Get all todos |
| GET | `/api/v1/sql/todo/{id}` | Get todo by ID |
| PUT | `/api/v1/sql/todo/{id}` | Update todo |
| PATCH | `/api/v1/sql/todo/{id}/toggle` | Toggle completion |
| DELETE | `/api/v1/sql/todo/{id}` | Delete todo |

### Testing with Insomnia

1. Import the collection (see above)
2. Select the **Development** environment
3. Start the application: `./mvnw spring-boot:run`
4. Test endpoints directly from Insomnia

## Testing

```bash
# Run all tests
./mvnw test

# Target: ≥80% code coverage
```

## Configuration

Key configuration areas:
- `application.yml` - Application settings
- `application-security.yml` - JWT/OAuth2 settings
- `application-database.yml` - Database connections
- `application-kafka.yml` - Kafka configuration
- `application-redis.yml` - Redis configuration

## Kafka Topics

Topic naming convention: `service-name.domain.event.v1`

- Producer abstraction included
- Consumer groups configured
- Dead-letter topic support

## Redis Usage

- Spring Cache abstraction
- Configurable TTL
- Distributed locking support

## Future Enhancements

- [ ] Kubernetes deployment manifests
- [ ] Rate limiting
- [ ] Multi-tenancy support
- [ ] Centralized logging (ELK/Loki)

## License

MIT License
