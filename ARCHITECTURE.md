# Modular Architecture Documentation

## Overview
This application has been refactored to follow a modular, reusable architecture with common utilities and services that can be shared across the entire application.

## Architecture Layers

### 1. Common Layer (`com.company.project.common`)
Reusable components that can be used across all modules.

#### Controllers (`common.controller`)
- **BaseController**: Generic base controller providing standard CRUD operations
  - Eliminates code duplication across controllers
  - Provides: create, findAll, findById, update, delete
  - Uses generic types for flexibility

#### Services (`common.service`)
- **CrudService**: Generic interface for CRUD operations
- **MapperService**: Generic interface for DTO/Entity conversions
- **ValidationService**: Programmatic validation using Jakarta Validation
- **ResponseBuilderService**: Standardized API response building
- **LoggingService**: Centralized logging with consistent formats
- **PaginationService**: Reusable pagination handling

#### Exception Handling (`common.exception`)
- **ResourceNotFoundException**: Custom exception for missing resources
- **GlobalExceptionHandler**: Centralized exception handling with @RestControllerAdvice
  - Handles validation errors
  - Handles resource not found
  - Handles generic exceptions

#### Repository (`common.repository`)
- **GenericRepositoryAdapter**: Generic repository interface for data access abstraction

#### Utilities (`common.util`)
- **ApiResponses**: Reusable OpenAPI response annotations
  - Todo-specific: TodoCreated, TodoFound, TodoUpdated, TodoDeleted, etc.
  - Generic: Created, Found, Updated, Deleted, ListRetrieved, BadRequest, Unauthorized, Forbidden
- **ApiResponseExamples**: Centralized response examples for API documentation
- **StringUtil**: String manipulation utilities
- **DateTimeUtil**: Date/time formatting and parsing utilities

### 2. Application Layer (`com.company.project.application`)
Business logic and service implementations.

- **TodoService**: Implements CrudService<Todo, String>
  - Standard CRUD operations
  - Custom business logic (toggleCompletion)
- **TodoMapperService**: Implements MapperService<Todo, TodoRequest, TodoResponse>
  - Converts between domain and DTO objects

### 3. Domain Layer (`com.company.project.domain`)
Core business entities and repository interfaces.

- **Todo**: Immutable domain entity
- **TodoRepository**: Domain repository interface

### 4. Presentation Layer (`com.company.project.presentation`)
REST controllers and API endpoints.

- **TodoController**: Extends BaseController
  - Minimal code, delegates to base controller
  - Custom endpoints (toggleCompletion)
- **HealthController**: Uses ResponseBuilderService

### 5. Infrastructure Layer (`com.company.project.infrastructure`)
Technical implementations (JPA, database, security, etc.)

- **SecurityConfig**: Spring Security configuration
  - Stateless session management
  - CSRF disabled (not needed for stateless APIs)
  - All requests permitted (trusts KrakenD gateway for access control)
  - Ready for JWT validation if needed in future

## Benefits of This Architecture

### 1. Reusability
- Common services can be used across all modules
- Generic base classes reduce code duplication
- Utilities are centralized and consistent

### 2. Maintainability
- Changes to common functionality affect all modules
- Consistent error handling across the application
- Centralized logging and validation

### 3. Scalability
- Easy to add new entities by extending BaseController
- New services can implement CrudService interface
- Consistent patterns across the codebase
- Gateway-based architecture allows horizontal scaling

### 4. Testability
- Services are interface-based for easy mocking
- Generic components can be tested once
- Clear separation of concerns

### 5. Security
- Access control handled at gateway level (KrakenD)
- Spring Boot trusts gateway for simplified internal security
- Easy to enable JWT validation when needed

## How to Add a New Entity

### Step 1: Create Domain Entity
```java
public class Product {
    private String id;
    private String name;
    // ... fields
}
```

### Step 2: Create Repository Interface
```java
public interface ProductRepository extends GenericRepositoryAdapter<Product, String> {
    // Custom queries if needed
}
```

### Step 3: Create Service
```java
@Service
public class ProductService implements CrudService<Product, String> {
    // Implement interface methods
}
```

### Step 4: Create Mapper
```java
@Service
public class ProductMapperService implements MapperService<Product, ProductRequest, ProductResponse> {
    // Implement toEntity and toResponse
}
```

### Step 5: Create Controller
```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductController extends BaseController<Product, ProductRequest, ProductResponse, String> {
    
    public ProductController(ProductService service, ProductMapperService mapper) {
        super(service, mapper, "Product");
    }
    
    @PostMapping
    @ApiResponses.Created
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return create(request);
    }
    
    // Other endpoints...
}
```

## Reusable Components Usage Examples

### Using ValidationService
```java
@Service
public class MyService {
    private final ValidationService validationService;
    
    public void process(MyDto dto) {
        validationService.validate(dto);
        // Process validated DTO
    }
}
```

### Using ResponseBuilderService
```java
@RestController
public class MyController {
    private final ResponseBuilderService responseBuilder;
    
    @GetMapping
    public ResponseEntity<ApiResponse<MyData>> getData() {
        MyData data = fetchData();
        return responseBuilder.success(data, "Data retrieved successfully");
    }
}
```

### Using LoggingService
```java
@Service
public class MyService {
    private final LoggingService loggingService;
    
    public void create(Entity entity) {
        // ... create logic
        loggingService.logCreate("Entity", entity.getId());
    }
}
```

### Using PaginationService
```java
@RestController
public class MyController {
    private final PaginationService paginationService;
    
    @GetMapping
    public ResponseEntity<PageResponse<MyData>> getPagedData(
            @RequestParam int page,
            @RequestParam int size) {
        Pageable pageable = paginationService.createPageable(page, size);
        Page<MyData> result = repository.findAll(pageable);
        return ResponseEntity.ok(paginationService.createPageResponse(result));
    }
}
```

### Using Utility Classes
```java
// String utilities
String truncated = StringUtil.truncate(longText, 100);
boolean isEmpty = StringUtil.isBlank(text);

// DateTime utilities
LocalDateTime now = DateTimeUtil.now();
String formatted = DateTimeUtil.formatDisplay(dateTime);
```

### Using Generic API Response Annotations
```java
@RestController
public class MyController {
    
    @PostMapping
    @ApiResponses.Created  // Generic annotation
    public ResponseEntity<MyResponse> create(@RequestBody MyRequest request) {
        // ...
    }
    
    @GetMapping("/{id}")
    @ApiResponses.Found  // Generic annotation
    public ResponseEntity<MyResponse> findById(@PathVariable String id) {
        // ...
    }
}
```

## Error Handling

All exceptions are handled centrally by `GlobalExceptionHandler`:
- **ResourceNotFoundException** → 404 with error details
- **MethodArgumentNotValidException** → 400 with validation errors
- **IllegalArgumentException** → 400 with error message
- **Generic Exception** → 500 with generic error message

## Best Practices

1. **Always extend BaseController** for standard CRUD operations
2. **Implement CrudService** for service layer consistency
3. **Use MapperService** for DTO conversions
4. **Leverage utility classes** instead of duplicating code
5. **Use generic annotations** from ApiResponses for API documentation
6. **Handle exceptions** by throwing appropriate exceptions (they'll be caught globally)
7. **Use LoggingService** for consistent logging patterns
8. **Validate inputs** using ValidationService or Jakarta Validation annotations

## Future Enhancements

- Add caching service for common queries
- Add audit service for tracking changes
- Add notification service for events
- Add search/filter service for complex queries
- Add export service for data export functionality
