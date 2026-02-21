# API Gateway Quick Start Guide

## Services Running

All services are now running successfully:

| Service | Port | URL | Status |
|---------|------|-----|--------|
| **KrakenD Gateway** | 8000 | http://localhost:8000 | ✅ Running |
| **Keycloak** | 8180 | http://localhost:8180 | ✅ Running |
| **Spring Boot App** | 8080 | http://localhost:8080 | Start manually |
| PostgreSQL | 5432 | localhost:5432 | ✅ Running |
| MongoDB | 27017 | localhost:27017 | ✅ Running |
| Redis | 6379 | localhost:6379 | ✅ Running |
| Kafka | 9092 | localhost:9092 | ✅ Running |

## Quick Test

### 1. Test KrakenD Health (No Auth Required)

```bash
curl http://localhost:8000/api/health
```

### 2. Start Your Spring Boot App

```bash
./mvnw spring-boot:run
```

### 3. Test Public Endpoints Through Gateway

All endpoints are currently public (no authentication required):

```bash
# Health check
curl http://localhost:8000/api/health

# Hello endpoint
curl http://localhost:8000/hello

# List todos
curl http://localhost:8000/api/v1/sql/todo

# Create todo
curl -X POST http://localhost:8000/api/v1/sql/todo \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Todo","description":"From KrakenD"}'

# Swagger UI
open http://localhost:8000/swagger-ui/index.html
```

## Current Configuration

### Security Model

The application currently uses a simplified security model:

- **KrakenD Gateway**: Handles routing and CORS, all endpoints are public
- **Spring Boot**: Stateless security with all requests permitted (trusts gateway)
- **Keycloak**: Available but not enforcing authentication (ready for production use)

This setup is ideal for:
- Development and testing
- Internal APIs behind a firewall
- Prototyping and demos

### Spring Boot Security

`SecurityConfig.java` is configured to:
- Disable CSRF (not needed for stateless APIs)
- Use stateless session management
- Permit all requests (relies on KrakenD for access control)

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
}
```

## Enabling Authentication (Optional)

To enable JWT authentication for production, follow these steps:

### Configure Keycloak (Required)

Follow the detailed setup in `KEYCLOAK_SETUP.md`:

1. **Access Keycloak Admin Console**: http://localhost:8180
   - Username: `admin`
   - Password: `admin`

2. **Create Realm**: `springboot-app`

3. **Configure Google OAuth**:
   - Get credentials from [Google Cloud Console](https://console.cloud.google.com/apis/credentials)
   - Update `.env` with your Google Client ID and Secret
   - Add Google as Identity Provider in Keycloak

4. **Create Client**: `springboot-backend`

5. **Create Roles**: `user`, `admin`

6. **Test Authentication**

## Architecture Flow

```
┌─────────┐      ┌─────────┐      ┌──────────┐      ┌─────────────┐
│ Client  │─────▶│ KrakenD │      │ Keycloak │      │ Spring Boot │
│         │      │  :8000  │      │  :8180   │      │    :8080    │
└─────────┘      └─────────┘      └──────────┘      └─────────────┘
                      │                  │                   │
                      │                  │                   │
                      └──────────────────┴───────────────────┘
                         Public Access (Auth Optional)
```

### Current Flow (Public Access)

1. **API Request**: Client sends request to KrakenD
2. **Routing**: KrakenD routes to Spring Boot
3. **Processing**: Spring Boot processes request
4. **Response**: Response returned to client

### Production Flow (With Authentication)

1. **User Login**: Client redirects to Keycloak for authentication
2. **Google OAuth**: User can login with Google account
3. **Token Issued**: Keycloak issues JWT access token
4. **API Request**: Client sends request to KrakenD with JWT in Authorization header
5. **JWT Validation**: KrakenD validates JWT signature and claims
6. **Token Relay**: KrakenD forwards valid request to Spring Boot
7. **Response**: Spring Boot processes request and returns response

## KrakenD Configuration

Current endpoints configured in `krakend/krakend.json` (all public):

- `GET /api/health` - Health check
- `GET /hello` - Hello endpoint
- `GET /actuator/{path}` - Spring Boot actuator
- `GET /api/v1/sql/todo` - List todos
- `GET /api/v1/sql/todo/{id}` - Get todo
- `POST /api/v1/sql/todo` - Create todo
- `PUT /api/v1/sql/todo/{id}` - Update todo
- `PATCH /api/v1/sql/todo/{id}/toggle` - Toggle todo
- `DELETE /api/v1/sql/todo/{id}` - Delete todo
- `GET /swagger-ui/{path}` - Swagger UI
- `GET /v3/api-docs/**` - OpenAPI docs
- `GET /swagger-resources/{path}` - Swagger resources

To add JWT authentication to specific endpoints, add the `extra_config` section with `auth/validator` (see KEYCLOAK_SETUP.md).

## Update Spring Boot to Validate JWT

Add to `src/main/resources/application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/springboot-app
          jwk-set-uri: http://localhost:8180/realms/springboot-app/protocol/openid-connect/certs
```

Update `SecurityConfig.java` to validate JWT tokens (see KEYCLOAK_SETUP.md for details).

## Testing with JWT

### Get Access Token

```bash
# Using password grant (for testing)
curl -X POST 'http://localhost:8180/realms/springboot-app/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=springboot-backend' \
  -d 'client_secret=YOUR_CLIENT_SECRET' \
  -d 'grant_type=password' \
  -d 'username=testuser' \
  -d 'password=password' | jq -r '.access_token'
```

### Use Token with KrakenD

```bash
TOKEN="your-access-token-here"

# List todos
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8000/api/v1/todos

# Create todo
curl -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Todo","description":"From KrakenD"}' \
  http://localhost:8000/api/v1/todos
```

## Troubleshooting

### KrakenD Returns 401

- Check JWT token is valid and not expired
- Verify realm name is `springboot-app` in Keycloak
- Check user has `user` role assigned
- Verify JWK URL in krakend.json matches Keycloak realm

### Keycloak Not Accessible

```bash
# Check Keycloak logs
podman compose logs keycloak

# Restart Keycloak
podman compose restart keycloak
```

### KrakenD Configuration Error

```bash
# Validate configuration
podman compose exec krakend krakend check -c /etc/krakend/krakend.json

# View logs
podman compose logs krakend
```

### Spring Boot Can't Validate JWT

- Verify `issuer-uri` in application.yml
- Check Spring Boot can reach Keycloak (network connectivity)
- Ensure OAuth2 Resource Server dependency is in pom.xml

## Useful Commands

```bash
# View all service logs
podman compose logs -f

# Restart specific service
podman compose restart keycloak

# Stop all services
podman compose down

# Stop and remove volumes (fresh start)
podman compose down -v

# Check service status
podman compose ps
```

## Security Notes

⚠️ **Current Configuration: Public Access**

All endpoints are currently public for development and testing:
- No authentication required
- Suitable for development, internal APIs, or prototyping
- Not recommended for production with sensitive data

⚠️ **Development Mode Settings**

Current configuration uses development defaults:
- Default passwords (change in production)
- HTTP only (use HTTPS in production)
- Permissive CORS (restrict in production)
- No rate limiting (add in production)

## Production Checklist

- [ ] Enable JWT authentication on protected endpoints
- [ ] Configure role-based access control
- [ ] Change all default passwords
- [ ] Enable HTTPS/TLS
- [ ] Configure proper CORS origins
- [ ] Enable rate limiting in KrakenD
- [ ] Configure token expiration policies
- [ ] Set up monitoring and logging
- [ ] Use secrets management (Vault, AWS Secrets Manager)
- [ ] Configure production database
- [ ] Set up backup and disaster recovery

## Additional Resources

- [KrakenD Documentation](https://www.krakend.io/docs/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/)
- Full setup guide: `KEYCLOAK_SETUP.md`
