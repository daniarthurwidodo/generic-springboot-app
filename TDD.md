# Technical Design Document (TDD)

## Enterprise Spring Boot 3 (Java 21) Boilerplate

Version: 1.0.0-SNAPSHOT

------------------------------------------------------------------------

## 1. Technology Stack

### Core Framework
- **Java 21** - Latest LTS with virtual threads and pattern matching
- **Spring Boot 3.2.3** - Enterprise application framework
- **Maven** - Build and dependency management

### Security
- **Spring Security** - Authentication and authorization
- **jjwt 0.12.5** - JWT token generation and validation
- **OAuth2 Client** - Social login integration

### Databases
- **PostgreSQL** - Primary relational database
- **MongoDB** - Document database
- **Spring Data JPA** - ORM abstraction
- **Spring Data MongoDB** - MongoDB integration
- **Flyway 10.10.0** - Database migration management

### Caching & Messaging
- **Redis** - Distributed cache and session storage
- **Apache Kafka** - Event streaming platform
- **Spring Kafka** - Kafka integration

### API & Documentation
- **SpringDoc OpenAPI 2.3.0** - API documentation
- **Swagger UI** - Interactive API explorer
- **Jakarta Validation** - Request validation

### Utilities
- **Lombok** - Boilerplate code reduction
- **ULID Creator 5.2.3** - Distributed ID generation
- **SLF4J/Logback** - Logging framework

### AI Integration
- **Spring AI MCP Server 1.0.0-M6** - Model Context Protocol server

### Testing
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **MockMvc** - Web layer testing
- **Spring Security Test** - Security testing
- **JaCoCo 0.8.11** - Code coverage
- **Embedded MongoDB (Flapdoodle)** - MongoDB testing

### Deployment
- **Docker/Podman** - Containerization
- **Docker Compose** - Multi-container orchestration

------------------------------------------------------------------------

## 2. Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (Controllers, DTOs, API Responses)     │
└─────────────────────────────────────────┘
              ↓ depends on
┌─────────────────────────────────────────┐
│         Application Layer               │
│    (Use Cases, Services, Logic)         │
└─────────────────────────────────────────┘
              ↓ depends on
┌─────────────────────────────────────────┐
│           Domain Layer                  │
│  (Entities, Interfaces, Business Rules) │
└─────────────────────────────────────────┘
              ↑ implemented by
┌─────────────────────────────────────────┐
│       Infrastructure Layer              │
│ (JPA, Security, Kafka, Redis, Config)   │
└─────────────────────────────────────────┘
```

**Dependency Rule:** Outer layers depend on inner layers only. Domain layer has no external dependencies.

### Layer Responsibilities

**Domain Layer** (`domain/`)
- Core business entities (Todo)
- Repository interfaces (TodoRepository)
- Business logic and rules
- No framework dependencies

**Application Layer** (`application/`)
- Use case implementations (TodoService)
- Business workflows
- Transaction boundaries
- MCP tools (McpToolsService)

**Infrastructure Layer** (`infrastructure/`)
- JPA entities and repositories (TodoJpaEntity, TodoJpaRepository)
- Repository implementations (TodoRepositoryImpl)
- Security configuration (SecurityConfig)
- OpenAPI configuration (OpenApiConfig)
- Lifecycle management (ApplicationLifecycleConfig)

**Presentation Layer** (`presentation/`)
- REST controllers (TodoController, HealthController, HelloController)
- DTOs (TodoRequest, TodoResponse, ErrorResponse)
- API response annotations (ApiResponses, ApiResponseExamples)
- Request/response mapping

------------------------------------------------------------------------

## 3. Package Structure

```
com.company.project
├── domain/
│   ├── Todo.java                    # Core entity
│   └── TodoRepository.java          # Repository interface
├── application/
│   ├── TodoService.java             # Business logic
│   └── McpToolsService.java         # MCP tools
├── infrastructure/
│   ├── TodoJpaEntity.java           # JPA entity
│   ├── TodoJpaRepository.java       # Spring Data repository
│   ├── TodoRepositoryImpl.java      # Repository implementation
│   ├── SecurityConfig.java          # Security configuration
│   ├── OpenApiConfig.java           # API documentation config
│   └── ApplicationLifecycleConfig.java  # Lifecycle hooks
└── presentation/
    ├── TodoController.java          # Todo REST API
    ├── HealthController.java        # Health check
    ├── HelloController.java         # Hello endpoint
    ├── ApiResponses.java            # Response annotations
    ├── ApiResponseExamples.java     # Example responses
    └── dto/
        ├── TodoRequest.java         # Create/update request
        ├── TodoResponse.java        # Todo response
        └── ErrorResponse.java       # Error response
```

------------------------------------------------------------------------

## 4. Security Design

### Current Implementation

**Spring Security Configuration:**
- CSRF disabled for API development
- CORS configured for cross-origin requests
- All endpoints require authentication by default
- Public endpoints: `/hello`, `/api/health`

**Security Filter Chain:**
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/hello", "/api/health").permitAll()
            .anyRequest().authenticated()
        )
        .build();
}
```

### JWT Flow (Foundation Ready)

1. **Authentication Request** → User credentials sent to `/auth/login`
2. **Token Generation** → JWT created with claims (user ID, roles, expiry)
3. **Token Response** → Access token + refresh token returned
4. **Request with Token** → Client includes `Authorization: Bearer <token>`
5. **Token Validation** → Filter validates signature and expiry
6. **Authorization** → RBAC enforces permissions

### OAuth2 Flow (Foundation Ready)

1. **Social Login** → Redirect to OAuth2 provider (Google/GitHub)
2. **Authorization** → User grants permissions
3. **Callback** → Provider returns authorization code
4. **Token Exchange** → Exchange code for access token
5. **User Info** → Fetch user profile
6. **Internal JWT** → Issue internal JWT for session management

------------------------------------------------------------------------

## 5. Database Design

### PostgreSQL Schema

**Todos Table (V1 Migration):**
```sql
CREATE TABLE todos (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**ULID Migration (V2):**
```sql
ALTER TABLE todos DROP CONSTRAINT todos_pkey;
ALTER TABLE todos DROP COLUMN id;
ALTER TABLE todos ADD COLUMN id VARCHAR(26) PRIMARY KEY;
```

### Entity Mapping

**Domain Entity (Todo.java):**
- Pure business object
- No JPA annotations
- Immutable where possible

**Infrastructure Entity (TodoJpaEntity.java):**
- JPA annotations (@Entity, @Table, @Id)
- Database-specific concerns
- Converts to/from domain entity

### Repository Pattern

**Interface (TodoRepository.java):**
```java
public interface TodoRepository {
    Todo save(Todo todo);
    Optional<Todo> findById(String id);
    List<Todo> findAll();
    void deleteById(String id);
}
```

**Implementation (TodoRepositoryImpl.java):**
- Uses Spring Data JPA repository
- Converts between domain and JPA entities
- Handles database exceptions

------------------------------------------------------------------------

## 6. API Design

### Versioning Strategy

- **URI-based versioning:** `/api/v1/...`
- Version in path for clarity
- Deprecated versions supported for 6 months
- Breaking changes require new version

### Response Format

**Success Response:**
```json
{
  "id": "01ARZ3NDEKTSV4RRFFQ69G5FAV",
  "title": "Sample Todo",
  "description": "Description",
  "completed": false,
  "createdAt": "2026-02-20T12:00:00Z",
  "updatedAt": "2026-02-20T12:00:00Z"
}
```

**Error Response:**
```json
{
  "timestamp": "2026-02-20T12:00:00Z",
  "status": 404,
  "error": "Todo not found",
  "path": "/api/v1/sql/todo/123"
}
```

### HTTP Status Codes

- `200 OK` - Successful GET, PUT, PATCH
- `201 Created` - Successful POST with Location header
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `405 Method Not Allowed` - Unsupported HTTP method
- `500 Internal Server Error` - Server errors

------------------------------------------------------------------------

## 7. Kafka Design

### Topic Naming Convention

Format: `service-name.domain.event.v1`

Examples:
- `todo-service.todo.created.v1`
- `todo-service.todo.updated.v1`
- `todo-service.todo.deleted.v1`

### Producer Pattern

```java
@Service
public class TodoEventProducer {
    private final KafkaTemplate<String, TodoEvent> kafkaTemplate;
    
    public void publishTodoCreated(Todo todo) {
        kafkaTemplate.send("todo-service.todo.created.v1", 
                          todo.getId(), 
                          new TodoCreatedEvent(todo));
    }
}
```

### Consumer Pattern

```java
@KafkaListener(topics = "todo-service.todo.created.v1", 
               groupId = "notification-service")
public void handleTodoCreated(TodoCreatedEvent event) {
    // Process event
}
```

### Dead Letter Topic

- Failed messages sent to `<topic-name>.dlt`
- Retry mechanism with exponential backoff
- Manual intervention for persistent failures

------------------------------------------------------------------------

## 8. Redis Design

### Cache Abstraction

```java
@Cacheable(value = "todos", key = "#id")
public Optional<Todo> getTodoById(String id) {
    return todoRepository.findById(id);
}

@CacheEvict(value = "todos", key = "#id")
public void deleteTodo(String id) {
    todoRepository.deleteById(id);
}
```

### Configuration

- Default TTL: Configurable per cache
- Eviction policy: LRU (Least Recently Used)
- Serialization: JSON

### Distributed Locking

```java
@Scheduled(fixedDelay = 60000)
@SchedulerLock(name = "scheduledTask", 
               lockAtMostFor = "50s", 
               lockAtLeastFor = "30s")
public void scheduledTask() {
    // Only one instance executes
}
```

------------------------------------------------------------------------

## 9. Spring AI MCP Server

### Configuration

```yaml
spring:
  ai:
    mcp:
      server:
        name: enterprise-spring-boot-mcp-server
        version: 1.0.0
        type: SYNC
        enabled: true
        stdio: false
        capabilities:
          tool: true
          resource: true
          prompt: true
          completion: true
```

### Tool Creation

```java
@Service
public class McpToolsService {
    
    @Tool(description = "Get a greeting message for the given name")
    public String greet(String name) {
        return "Hello, " + (name != null ? name : "World") + "!";
    }
    
    @Tool(description = "Get application health status")
    public String getHealthStatus() {
        return "Application is running";
    }
}
```

### Tool Registration

```java
@Bean
public ToolCallbackProvider toolCallbackProvider(McpToolsService service) {
    return MethodToolCallbackProvider.builder()
            .toolObjects(service)
            .build();
}
```

------------------------------------------------------------------------

## 10. Testing Strategy

### 10.1 Framework & Tools
- **JUnit 5** - Test framework
- **Mockito** - Mocking dependencies
- **MockMvc** - Web layer testing
- **Spring Security Test** - `@WithMockUser` for auth
- **JaCoCo** - Coverage reporting (≥80% target)

### 10.2 Test Method Naming
- Use **camelCase** (not snake_case)
- Patterns:
  - `<method>Should<expectedBehavior>` (e.g., `helloShouldReturnHelloWorld`)
  - `should<expectedBehavior>` (e.g., `shouldReturnHelloWorldMessage`)

### 10.3 Test Structure
- Happy path scenarios
- Error scenarios (4xx, 5xx)
- Content-type validation
- Unsupported HTTP methods (405)

### 10.4 Web Layer Testing Pattern

```java
@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @Test
    @WithMockUser
    void helloShouldReturnHelloWorld() throws Exception {
        mockMvc.perform(get("/hello"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("Hello World"));
    }

    @Test
    @WithMockUser
    void postShouldReturnMethodNotAllowed() throws Exception {
        mockMvc.perform(post("/hello"))
            .andExpect(status().isMethodNotAllowed());
    }
}
```

### 10.5 Coverage Exclusions

JaCoCo excludes:
- Domain entities (POJOs)
- DTOs (data transfer objects)
- Configuration classes

------------------------------------------------------------------------

## 11. Docker Compose Services

### Application Service
```yaml
app:
  build: .
  ports:
    - "8080:8080"
  environment:
    - SPRING_PROFILES_ACTIVE=docker
  depends_on:
    - postgres
    - mongodb
    - redis
    - kafka
```

### Infrastructure Services

**PostgreSQL:**
- Port: 5432
- Database: appdb
- User: appuser

**MongoDB:**
- Port: 27017
- Database: appdb
- User: appuser

**Redis:**
- Port: 6379
- No authentication (dev mode)

**Kafka:**
- Port: 9092
- Requires Zookeeper

**Zookeeper:**
- Port: 2181
- Kafka coordination

------------------------------------------------------------------------

## 12. Configuration Management

### Profile-Based Configuration

- `application.yml` - Default configuration
- `application-docker.yml` - Docker overrides
- Environment variables for secrets

### Key Configuration Areas

**Database:**
- Connection URLs
- Pool sizes
- JPA settings

**Security:**
- JWT secret key
- Token expiry
- OAuth2 client credentials

**Kafka:**
- Bootstrap servers
- Consumer groups
- Topic configurations

**Redis:**
- Host and port
- Connection pool
- TTL settings

**Actuator:**
- Exposed endpoints
- Health check details
- Metrics configuration

------------------------------------------------------------------------

## 13. Logging Strategy

### Logback Configuration

- Console appender for development
- File appender for production
- Rolling file policy (10MB per file, 30 days retention)
- JSON format for centralized logging

### Log Levels

- `ERROR` - Critical errors requiring immediate attention
- `WARN` - Warning conditions (e.g., resource not found)
- `INFO` - Important business events
- `DEBUG` - Detailed diagnostic information
- `TRACE` - Very detailed diagnostic information

### Logging Pattern

```
%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

------------------------------------------------------------------------

## 14. Future Enhancements

### Phase 2: Authentication
- Complete JWT implementation
- Refresh token mechanism
- User management endpoints
- Password encryption (BCrypt)

### Phase 3: Advanced Features
- Rate limiting (Bucket4j)
- API key authentication
- Multi-tenancy (tenant ID in requests)
- Audit logging (who, what, when)

### Phase 4: Operations
- Kubernetes manifests
- Helm charts
- Prometheus metrics
- Grafana dashboards
- Distributed tracing (Jaeger)

### Phase 5: Scalability
- Event sourcing
- CQRS pattern
- Service mesh (Istio)
- API gateway (Spring Cloud Gateway)
