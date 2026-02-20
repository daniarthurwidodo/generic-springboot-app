# Enterprise Spring Boot 3 Backend Boilerplate

A reusable, enterprise-grade backend template built with **Spring Boot 3.2.3** and **Java 21**, following Clean Architecture principles. Designed for modular monolith architecture with microservices transition support.

## Features

### Core Functionality
- Todo Management API with CRUD operations
- Health check and monitoring endpoints
- Spring AI MCP Server integration for AI assistant tools
- OpenAPI 3.0 specification with Swagger UI

### Authentication & Security
- Spring Security configuration
- JWT Authentication support (ready for implementation)
- OAuth2 client support (ready for implementation)
- Role-Based Access Control (RBAC) foundation
- OWASP-compliant security practices

### Database & Storage
- **PostgreSQL** - Primary RDBMS with JPA/Hibernate
- **MongoDB** - Document database support
- **Flyway** - Database migrations with version control
- **ULID** - Universally Unique Lexicographically Sortable Identifiers

### Messaging & Caching
- **Apache Kafka** - Event-driven architecture support
- **Redis** - Caching and distributed locking

### API Design
- RESTful APIs with URI versioning (`/api/v1/...`)
- Standardized response format with timestamps
- Comprehensive error handling
- Target: <200ms average latency

## Tech Stack

| Component | Technology | Version |
|-----------|------------|---------|
| Runtime | Java | 21 |
| Framework | Spring Boot | 3.2.3 |
| Security | Spring Security, JWT (jjwt), OAuth2 | 0.12.5 |
| Databases | PostgreSQL, MongoDB | Latest |
| Cache | Redis | Latest |
| Messaging | Apache Kafka | Latest |
| Migrations | Flyway | 10.10.0 |
| AI Integration | Spring AI MCP Server | 1.0.0-M6 |
| API Docs | SpringDoc OpenAPI | 2.3.0 |
| ID Generation | ULID Creator | 5.2.3 |
| Testing | JUnit 5, Mockito, MockMvc, Spring Security Test | Latest |
| Code Coverage | JaCoCo | 0.8.11 |
| Build Tool | Maven | 3.x |
| Deployment | Docker/Podman, Docker Compose | Latest |

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

| Method | Endpoint | Description | Status Code |
|--------|----------|-------------|-------------|
| GET | `/api/health` | Health check | 200 |
| GET | `/hello` | Hello endpoint | 200 |
| POST | `/api/v1/sql/todo` | Create todo | 201 |
| GET | `/api/v1/sql/todo` | Get all todos | 200 |
| GET | `/api/v1/sql/todo/{id}` | Get todo by ID (ULID) | 200, 404 |
| PUT | `/api/v1/sql/todo/{id}` | Update todo | 200, 404 |
| PATCH | `/api/v1/sql/todo/{id}/toggle` | Toggle completion status | 200, 404 |
| DELETE | `/api/v1/sql/todo/{id}` | Delete todo | 204, 404 |

Note: Todo IDs use ULID format (e.g., `01ARZ3NDEKTSV4RRFFQ69G5FAV`)

### Testing with Insomnia

1. Import the collection (see above)
2. Select the **Development** environment
3. Start the application: `./mvnw spring-boot:run`
4. Test endpoints directly from Insomnia

## Testing

```bash
# Run all tests
./mvnw test

# Run tests with coverage report
./mvnw clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

Test coverage target: ≥80% (excludes domain, dto, and config packages)

Test naming convention: Use camelCase (e.g., `helloShouldReturnHelloWorld`)

## Configuration

Key configuration files:
- `application.yml` - Main application settings (database, Redis, Kafka, MCP server)
- `application-docker.yml` - Docker-specific overrides
- `logback-spring.xml` - Logging configuration

Configuration includes:
- PostgreSQL connection (localhost:5432)
- MongoDB connection (localhost:27017)
- Redis connection (localhost:6379)
- Kafka bootstrap servers (localhost:9092)
- Spring AI MCP Server settings
- Actuator endpoints (health, info, metrics)
- Graceful shutdown with 30s timeout

## Spring AI MCP Server

The application includes a Model Context Protocol (MCP) server for AI assistant integration:

```yaml
spring.ai.mcp.server:
  name: enterprise-spring-boot-mcp-server
  version: 1.0.0
  type: SYNC
  enabled: true
  capabilities: tool, resource, prompt, completion
```

Create custom tools by annotating service methods with `@Tool`:

```java
@Tool(description = "Get a greeting message")
public String greet(String name) {
    return "Hello, " + name + "!";
}
```

## Kafka Topics

Topic naming convention: `service-name.domain.event.v1`

- Producer abstraction ready
- Consumer groups configurable
- Dead-letter topic support planned

## Redis Usage

- Spring Cache abstraction
- Configurable TTL
- Distributed locking support ready

## Project Structure

```
src/main/java/com/company/project/
├── domain/              # Entities, business logic, repository interfaces
├── application/         # Use cases, services (TodoService, McpToolsService)
├── infrastructure/      # JPA, security, lifecycle, OpenAPI config
└── presentation/        # REST controllers, DTOs, API responses
```

## Future Enhancements

- [ ] Complete JWT authentication implementation
- [ ] OAuth2 social login integration
- [ ] Kubernetes deployment manifests
- [ ] Rate limiting middleware
- [ ] Multi-tenancy support
- [ ] Centralized logging (ELK/Loki)
- [ ] API versioning strategy
- [ ] Comprehensive integration tests

## License

MIT License
