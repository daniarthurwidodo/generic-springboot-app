# Project Constitution

## Object-Oriented Programming (OOP) Principles

### Encapsulation
- Keep fields `private` and expose them through controlled accessors (getters/setters) when necessary
- Favor immutable data structures using records or final fields where possible
- Hide internal implementation details; expose only what's necessary through public APIs
- Use constructor injection for dependencies (e.g., `@RequiredArgsConstructor` with Lombok)

### Abstraction
- Program to interfaces, not implementations (e.g., `TodoRepository` interface, not `TodoJpaRepository`)
- Use abstract classes or interfaces to define contracts
- Hide complexity behind well-defined boundaries (e.g., service layer hides repository logic)
- Prefer domain-driven design with clear separation between domain, application, and infrastructure layers

### Inheritance
- Favor composition over inheritance when building complex types
- Use inheritance primarily for:
  - Extending framework base classes (e.g., JPA entities)
  - Creating shared base classes with common behavior
- Mark classes as `final` when not designed for extension
- Avoid deep inheritance hierarchies; prefer shallow and wide structures

### Polymorphism
- Leverage interface-based polymorphism for flexibility and testability
- Use strategy pattern for interchangeable algorithms
- Allow runtime behavior through method overriding and interface implementations
- Prefer dependency injection to enable polymorphic behavior at runtime

## Naming Conventions

### Test Methods
- Use **camelCase** for test method names (e.g., `helloShouldReturnHelloWorld`, `shouldReturnHelloWorldMessage`)
- Do **not** use snake_case (e.g., avoid `hello_should_return_hello_world`)
- Preferred patterns:
  - `<method>Should<expectedBehavior>` (e.g., `helloShouldReturnHelloWorld`)
  - `should<expectedBehavior>` (e.g., `shouldReturnHelloWorldMessage`)
