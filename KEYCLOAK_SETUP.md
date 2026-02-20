# Keycloak + KrakenD + Google OAuth Setup Guide

## Overview

This guide walks you through setting up:
- **Keycloak** as the identity provider
- **Google OAuth** for social login
- **KrakenD** as the API Gateway with JWT validation
- **Spring Boot** as the OAuth2 Resource Server

## Architecture

```
User → KrakenD (Port 8000) → Spring Boot (Port 8080)
         ↓
      Keycloak (Port 8180) ← Google OAuth
```

## Prerequisites

1. Google Cloud Console account
2. Docker/Podman installed
3. `.env` file configured (see below)

## Step 1: Configure Google OAuth

### 1.1 Create Google OAuth Credentials

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Navigate to **APIs & Services** → **Credentials**
4. Click **Create Credentials** → **OAuth 2.0 Client ID**
5. Configure OAuth consent screen if prompted
6. Application type: **Web application**
7. Add authorized redirect URIs:
   ```
   http://localhost:8180/realms/springboot-app/broker/google/endpoint
   ```
8. Copy the **Client ID** and **Client Secret**

### 1.2 Update .env File

Edit `.env` and add your Google credentials:

```bash
GOOGLE_CLIENT_ID=your-actual-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-actual-google-client-secret
```

## Step 2: Start Services

```bash
# Start all services
podman compose up -d

# Check status
podman compose ps

# View logs
podman compose logs -f keycloak
```

Wait for Keycloak to be ready (takes ~60 seconds on first start).

## Step 3: Configure Keycloak

### 3.1 Access Keycloak Admin Console

1. Open: http://localhost:8180
2. Click **Administration Console**
3. Login:
   - Username: `admin`
   - Password: `admin`

### 3.2 Create Realm

1. Click dropdown at top-left (currently shows "master")
2. Click **Create Realm**
3. Realm name: `springboot-app`
4. Click **Create**

### 3.3 Configure Google Identity Provider

1. In `springboot-app` realm, go to **Identity Providers**
2. Click **Google**
3. Configure:
   - **Client ID**: Paste from Google Console
   - **Client Secret**: Paste from Google Console
   - **Default Scopes**: `openid profile email`
4. Click **Save**
5. Copy the **Redirect URI** (should match what you added in Google Console)

### 3.4 Create Client for Spring Boot App

1. Go to **Clients** → **Create client**
2. Configure:
   - **Client type**: OpenID Connect
   - **Client ID**: `springboot-backend`
   - Click **Next**
3. Capability config:
   - **Client authentication**: ON
   - **Authorization**: OFF
   - **Standard flow**: ON
   - **Direct access grants**: ON
   - Click **Next**
4. Login settings:
   - **Valid redirect URIs**: `http://localhost:8080/*`
   - **Valid post logout redirect URIs**: `http://localhost:8080/*`
   - **Web origins**: `http://localhost:8080`
   - Click **Save**
5. Go to **Credentials** tab
6. Copy the **Client Secret** (you'll need this for Spring Boot)

### 3.5 Create Client for Frontend (Optional)

If you have a frontend app:

1. Go to **Clients** → **Create client**
2. Configure:
   - **Client ID**: `frontend-app`
   - **Client type**: OpenID Connect
3. Capability config:
   - **Client authentication**: OFF (public client)
   - **Standard flow**: ON
   - **Direct access grants**: OFF
4. Login settings:
   - **Valid redirect URIs**: `http://localhost:3000/*`
   - **Valid post logout redirect URIs**: `http://localhost:3000/*`
   - **Web origins**: `http://localhost:3000`

### 3.6 Create Roles

1. Go to **Realm roles** → **Create role**
2. Create roles:
   - `user` (default role for all users)
   - `admin` (for admin users)

### 3.7 Set Default Roles

1. Go to **Realm settings** → **User registration**
2. Click **Default roles** tab
3. Add `user` role as default

### 3.8 Create Test User (Optional)

1. Go to **Users** → **Add user**
2. Username: `testuser`
3. Email: `test@example.com`
4. Click **Create**
5. Go to **Credentials** tab
6. Set password: `password`
7. Temporary: OFF
8. Click **Set password**
9. Go to **Role mapping** tab
10. Assign `user` role

## Step 4: Update Spring Boot Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/springboot-app
          jwk-set-uri: http://localhost:8180/realms/springboot-app/protocol/openid-connect/certs
```

## Step 5: Test the Setup

### 5.1 Test Keycloak

Get an access token:

```bash
curl -X POST 'http://localhost:8180/realms/springboot-app/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=springboot-backend' \
  -d 'client_secret=YOUR_CLIENT_SECRET' \
  -d 'grant_type=password' \
  -d 'username=testuser' \
  -d 'password=password'
```

### 5.2 Test KrakenD Gateway

Health check (no auth required):
```bash
curl http://localhost:8000/health
```

Protected endpoint (requires JWT):
```bash
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  http://localhost:8000/api/v1/todos
```

### 5.3 Test Google Login

1. Open: http://localhost:8180/realms/springboot-app/account
2. Click **Sign in**
3. Click **Google** button
4. Login with your Google account
5. You should be redirected back to Keycloak

## Step 6: Frontend Integration

### Login Flow

```javascript
// Redirect to Keycloak login
const loginUrl = 'http://localhost:8180/realms/springboot-app/protocol/openid-connect/auth?' +
  'client_id=frontend-app&' +
  'redirect_uri=http://localhost:3000/callback&' +
  'response_type=code&' +
  'scope=openid profile email';

window.location.href = loginUrl;
```

### Exchange Code for Token

```javascript
// After redirect, exchange code for token
const response = await fetch('http://localhost:8180/realms/springboot-app/protocol/openid-connect/token', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
  },
  body: new URLSearchParams({
    grant_type: 'authorization_code',
    client_id: 'frontend-app',
    code: authorizationCode,
    redirect_uri: 'http://localhost:3000/callback',
  }),
});

const { access_token } = await response.json();
```

### Call API through KrakenD

```javascript
const response = await fetch('http://localhost:8000/api/v1/todos', {
  headers: {
    'Authorization': `Bearer ${access_token}`,
  },
});
```

## Ports Reference

| Service | Port | URL |
|---------|------|-----|
| KrakenD Gateway | 8000 | http://localhost:8000 |
| Spring Boot App | 8080 | http://localhost:8080 |
| Keycloak | 8180 | http://localhost:8180 |
| PostgreSQL | 5432 | localhost:5432 |
| MongoDB | 27017 | localhost:27017 |
| Redis | 6379 | localhost:6379 |
| Kafka | 9092 | localhost:9092 |

## Troubleshooting

### Keycloak not starting
```bash
# Check logs
podman compose logs keycloak

# Restart
podman compose restart keycloak
```

### KrakenD JWT validation failing
- Verify realm name is `springboot-app`
- Check JWK URL in krakend.json matches Keycloak realm
- Ensure token is not expired

### Google OAuth not working
- Verify redirect URI in Google Console matches exactly
- Check Google Client ID and Secret in Keycloak
- Ensure OAuth consent screen is configured

### Spring Boot not validating tokens
- Check `issuer-uri` matches Keycloak realm
- Verify `jwk-set-uri` is accessible
- Check Spring Security configuration

## Security Notes

1. **Change default passwords** in production
2. **Use HTTPS** in production
3. **Secure client secrets** (use environment variables)
4. **Enable CSRF protection** for web apps
5. **Configure token expiration** appropriately
6. **Use refresh tokens** for long-lived sessions

## Next Steps

1. Configure token expiration policies
2. Add custom claims to JWT
3. Implement refresh token flow
4. Add rate limiting in KrakenD
5. Set up monitoring and logging
6. Configure production SSL/TLS
