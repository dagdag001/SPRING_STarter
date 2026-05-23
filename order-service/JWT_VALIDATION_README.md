# JWT Validation Feature - Order Service

## Overview

This document describes the JWT (JSON Web Token) validation feature implemented in the Order Service. This optional feature adds authentication and authorization to protect the `/api/orders` endpoints.

## Requirements

This implementation satisfies the following requirements:
- **Requirement 15.1**: Requests without JWT token return HTTP 401 Unauthorized
- **Requirement 15.2**: Requests with invalid JWT token return HTTP 401 Unauthorized
- **Requirement 15.3**: Requests with expired JWT token return HTTP 401 Unauthorized
- **Requirement 15.4**: Requests with valid JWT token are processed normally

## Architecture

### Components

1. **JwtAuthenticationFilter** (`presentation/security/JwtAuthenticationFilter.java`)
   - Extends `OncePerRequestFilter` to intercept all HTTP requests
   - Extracts JWT token from `Authorization` header (format: `Bearer <token>`)
   - Validates token signature and expiration
   - Sets authentication in Spring Security's `SecurityContext`

2. **SecurityConfig** (`infrastructure/config/SecurityConfig.java`)
   - Configures Spring Security's `SecurityFilterChain`
   - Protects `/api/orders/**` endpoints (requires authentication)
   - Allows public access to `/h2-console/**` and `/actuator/**` (development)
   - Disables CSRF (not needed for stateless JWT authentication)
   - Configures stateless session management

### Configuration

The JWT secret key must match the Auth Service configuration:

```yaml
jwt:
  secret: my-secret-key-that-is-at-least-256-bits-long-for-security
  expiration-hours: 1
```

**Important**: In production, use environment variables for the secret key:
```bash
export JWT_SECRET="your-production-secret-key-here"
```

## How It Works

### Request Flow

1. **Client sends request** with JWT token:
   ```
   GET /api/orders/123
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

2. **JwtAuthenticationFilter intercepts** the request:
   - Extracts token from `Authorization` header
   - Validates token signature using the secret key
   - Checks token expiration
   - Extracts user information (userId, username)

3. **If token is valid**:
   - Creates `UsernamePasswordAuthenticationToken`
   - Sets authentication in `SecurityContext`
   - Request proceeds to controller

4. **If token is invalid/missing/expired**:
   - Authentication is not set
   - Spring Security returns HTTP 401 Unauthorized

### Token Validation

The filter validates:
- **Signature**: Token must be signed with the correct secret key
- **Expiration**: Token must not be expired
- **Format**: Token must be well-formed JWT

### Token Structure

Expected JWT claims:
```json
{
  "sub": "user123",           // User ID (subject)
  "username": "john_doe",     // Username
  "iat": 1705320000,          // Issued at timestamp
  "exp": 1705323600           // Expiration timestamp
}
```

## Usage Examples

### Obtaining a JWT Token

First, authenticate with the Auth Service:

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePass123!"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresAt": "2024-01-15T18:30:00Z"
}
```

### Creating an Order (Authenticated)

```bash
curl -X POST http://localhost:8082/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -d '{
    "customerId": "cust123",
    "items": [
      {
        "productId": "prod-001",
        "quantity": 2,
        "price": 29.99
      }
    ]
  }'
```

### Getting an Order (Authenticated)

```bash
curl -X GET http://localhost:8082/api/orders/order123 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Error Responses

**Missing Token (401 Unauthorized)**:
```bash
curl -X GET http://localhost:8082/api/orders/order123
```

Response:
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 401,
  "error": "Unauthorized",
  "path": "/api/orders/order123"
}
```

**Invalid Token (401 Unauthorized)**:
```bash
curl -X GET http://localhost:8082/api/orders/order123 \
  -H "Authorization: Bearer invalid.token.here"
```

**Expired Token (401 Unauthorized)**:
```bash
curl -X GET http://localhost:8082/api/orders/order123 \
  -H "Authorization: Bearer <expired-token>"
```

## Testing

### Unit Tests

**JwtAuthenticationFilterTest** tests:
- Valid JWT token sets authentication
- Missing Authorization header does not set authentication
- Invalid JWT token does not set authentication
- Expired JWT token does not set authentication
- Malformed Authorization header does not set authentication
- Filter continues even on exception

### Integration Tests

**SecurityConfigIntegrationTest** tests:
- Protected endpoint without token returns 401
- Protected endpoint with invalid token returns 401
- Protected endpoint with expired token returns 401
- Protected endpoint with valid token allows access
- Create order without token returns 401
- Create order with valid token allows access
- H2 console is accessible without authentication

### Running Tests

```bash
# Run all tests
./mvnw test -pl order-service

# Run only security tests
./mvnw test -pl order-service -Dtest=*Security*,*Jwt*
```

## Security Considerations

### Production Deployment

1. **Secret Key Management**:
   - Never commit secret keys to version control
   - Use environment variables or secret management systems (AWS Secrets Manager, HashiCorp Vault)
   - Rotate keys periodically

2. **HTTPS**:
   - Always use HTTPS in production to prevent token interception
   - Configure SSL/TLS certificates

3. **Token Expiration**:
   - Use short expiration times (1 hour recommended)
   - Implement token refresh mechanism for better UX

4. **CORS Configuration**:
   - Configure CORS policies to restrict allowed origins
   - Only allow trusted domains

5. **Rate Limiting**:
   - Implement rate limiting to prevent brute force attacks
   - Use Spring Cloud Gateway or API Gateway

### Common Security Issues

❌ **Don't**:
- Store JWT tokens in localStorage (vulnerable to XSS)
- Use weak secret keys (< 256 bits)
- Disable HTTPS in production
- Log JWT tokens

✅ **Do**:
- Store tokens in httpOnly cookies or memory
- Use strong, randomly generated secret keys
- Enable HTTPS in production
- Log authentication failures for monitoring

## Troubleshooting

### Issue: 401 Unauthorized with valid token

**Possible causes**:
1. Secret key mismatch between Auth Service and Order Service
2. Token expired
3. Token format incorrect (missing "Bearer " prefix)

**Solution**:
- Verify `jwt.secret` matches in both services
- Check token expiration time
- Ensure Authorization header format: `Bearer <token>`

### Issue: Filter not being applied

**Possible causes**:
1. SecurityConfig not being loaded
2. Filter not registered in Spring context

**Solution**:
- Verify `@Configuration` and `@EnableWebSecurity` annotations
- Check `@Component` annotation on JwtAuthenticationFilter
- Review Spring Boot logs for configuration errors

### Issue: H2 Console not accessible

**Possible causes**:
1. Security configuration blocking H2 console
2. Frame options preventing display

**Solution**:
- Verify `/h2-console/**` is in `permitAll()` list
- Check frame options configuration: `.frameOptions().sameOrigin()`

## Disabling JWT Validation

To disable JWT validation (for development/testing):

1. Comment out `@EnableWebSecurity` in `SecurityConfig`
2. Or create a test profile without security:

```yaml
# application-test.yml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

Run with test profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=test
```

## Future Enhancements

Potential improvements:
1. **Token Refresh**: Implement refresh token mechanism
2. **Role-Based Access Control**: Add roles/permissions to JWT claims
3. **Token Revocation**: Implement token blacklist/whitelist
4. **OAuth2 Integration**: Support OAuth2/OpenID Connect
5. **Multi-tenancy**: Support tenant-specific authentication

## References

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/) - JWT debugger and documentation
- [JJWT Library](https://github.com/jwtk/jjwt) - Java JWT library
- [OWASP JWT Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/JSON_Web_Token_for_Java_Cheat_Sheet.html)
