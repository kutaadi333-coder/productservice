# Security Documentation

## 1. Authentication

The Product Service uses Spring Security with JWT-based authentication.

Authentication flow:

1. User registers using the registration API.
2. Password is encrypted using BCrypt before being stored.
3. User logs in using email and password.
4. The service validates the credentials.
5. An access token and refresh token are generated.
6. The access token is required for protected APIs.
7. JWT requests are validated by the JWT authentication filter.

## 2. Password Security

Passwords are never stored as plain text.

BCrypt password hashing is used before storing user passwords in the database.

Example stored password format:

`$2...`

The original password cannot be retrieved from the stored BCrypt hash.

## 3. JWT Security

JWT tokens are used for stateless authentication.

The JWT contains information such as:

- User ID
- Email
- Role
- Token type
- Issued-at time
- Expiration time

Two token types are supported:

- ACCESS
- REFRESH

Only ACCESS tokens are accepted for protected Product APIs.

## 4. JWT Secret Management

The JWT secret is not hard-coded in the source code.

The application reads the secret from the environment variable:

`JWT_SECRET`

Application configuration:

```yaml
jwt:
  secret: ${JWT_SECRET}