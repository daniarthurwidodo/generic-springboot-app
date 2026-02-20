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

-   JUnit 5
-   Mockito
-   MockMvc
-   ≥80% coverage target

------------------------------------------------------------------------

## 9. Docker Compose Services

-   app
-   postgres
-   mongodb
-   redis
-   kafka
-   zookeeper
