# Technical Design Document (TDD)

## Enterprise Spring Boot 3 (Java 21) Boilerplate

------------------------------------------------------------------------

## 1. Technology Stack

-   Java 21
-   Spring Boot 3
-   Spring Security
-   JWT + OAuth2
-   PostgreSQL + MongoDB
-   Redis
-   Kafka
-   Flyway
-   Docker

------------------------------------------------------------------------

## 2. Architecture

Clean Architecture Layers:

-   Presentation Layer (Controllers)
-   Application Layer (Use Cases)
-   Domain Layer (Entities, Interfaces)
-   Infrastructure Layer (DB, Kafka, Redis, Security)

Dependency Rule: Outer layers depend on inner layers only.

------------------------------------------------------------------------

## 3. Package Structure

com.company.project ├── domain ├── application ├── infrastructure └──
presentation

------------------------------------------------------------------------

## 4. Security Design

JWT Flow: 1. Authenticate 2. Issue token 3. Validate via filter 4.
Enforce RBAC

OAuth2 Flow: - Social login - Issue internal JWT

------------------------------------------------------------------------

## 5. API Versioning

-   URI-based versioning
-   /api/v1/
-   Deprecated versions supported for 6 months

------------------------------------------------------------------------

## 6. Kafka Design

Topic Naming: service-name.domain.event.v1

-   Producer abstraction
-   Consumer groups
-   Dead-letter topic

------------------------------------------------------------------------

## 7. Redis Design

-   Spring Cache abstraction
-   TTL configuration
-   Distributed locking

------------------------------------------------------------------------

## 8. Testing Strategy

### 8.1 Framework & Tools
- JUnit 5
- Mockito
- MockMvc
- Spring Security Test (`@WithMockUser`)
- ≥80% coverage target

### 8.2 Test Method Naming
- Use **camelCase** for test method names
- Do **not** use snake_case
- Preferred patterns:
  - `<method>Should<expectedBehavior>` (e.g., `helloShouldReturnHelloWorld`)
  - `should<expectedBehavior>` (e.g., `shouldReturnHelloWorldMessage`)

### 8.3 Test Structure
- Test happy path scenarios
- Test error scenarios (4xx, 5xx responses)
- Test response content-type headers
- Test unsupported HTTP methods (405 Method Not Allowed)

### 8.4 Web Layer Testing Pattern
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

### 8.5 Security Testing
- Use `@WithMockUser` for authenticated requests
- Mock `SecurityFilterChain` to disable CSRF in tests
- Test both authorized and unauthorized scenarios

------------------------------------------------------------------------

## 9. Docker Compose Services

-   app
-   postgres
-   mongodb
-   redis
-   kafka
-   zookeeper
