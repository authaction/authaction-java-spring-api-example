# Integration Authorization in Spring Boot API using AuthAction

This is a Spring Boot application demonstrating how to integrate API authorization using AuthAction with Spring Security OAuth2 Resource Server and JWKS for token validation.

## Overview

This application showcases how to configure and handle authorization using AuthAction's access token in a Spring Boot REST API. It uses JSON Web Tokens (JWT) for authentication and authorization and validates them dynamically using JWKS (JSON Web Key Sets) provided by AuthAction.

## Prerequisites

Before using this application, ensure you have:

- Java 17+ installed
- Maven installed (for building and running the project)
- AuthAction API credentials:
  - Tenant Domain
  - API Identifier (Audience)

## Installation

1. Clone the repository:
```bash
git clone git@github.com:authaction/authaction-java-spring-api-example.git
cd authaction-java-spring-api-example
```

2. Install dependencies:
```bash
./mvnw clean install
```

## Configuration

Edit the `src/main/resources/application.properties` file:

```properties
spring.application.name=springoauth2demo
server.port=3000

authaction.audience=your-authaction-api-identifier
authaction.domain=your-authaction-tenant-domain
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://${authaction.domain}/
```

Replace the `authaction.audience` and `authaction.domain` with your actual AuthAction API configuration values.

## Usage

Start the Spring Boot server:
```bash
./mvnw spring-boot:run
```

This will start the application at: http://localhost:3000

## Testing Authorization

### Obtaining an Access Token

To obtain an access token via client credentials flow, run the following curl command:

```bash
curl --request POST \
  --url https://your-authaction-tenant-domain/oauth2/m2m/token \
  --header 'content-type: application/json' \
  --data '{
    "client_id": "your-authaction-app-clientid",
    "client_secret": "your-authaction-app-client-secret",
    "audience": "your-authaction-api-identifier",
    "grant_type": "client_credentials"
  }'
```

Replace the placeholders with your actual AuthAction credentials.

### Public Endpoint

You can call the public API without any authentication token. The GET `/public` endpoint is accessible by any user:

```bash
curl --request GET \
  --url http://localhost:3000/public
```

Response:
```json
{
  "message": "This is a public message!"
}
```

### Protected Endpoint

To access the protected API, you must send a valid JWT token:

```bash
curl --request GET \
  --url http://localhost:3000/protected \
  --header "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  --header "content-type: application/json"
```

Response:
```json
{
  "message": "This is a protected message!"
}
```

## Code Explanation

### Security Configuration (`SecurityConfig.java`)

- Integrates JWT authentication into the Spring Boot application
- Configures JWT validation using OAuth2 Resource Server and JWKS URI

Security Filter Chain:
- `.requestMatchers("/public").permitAll()` - Public endpoint, no authentication required
- `.anyRequest().authenticated()` - All other endpoints require a valid JWT token
- `.oauth2ResourceServer().jwt()` - Tells Spring to validate incoming requests using JWT

### Audience Validator (`AudienceValidator.java`)

- Custom validator that checks if the JWT's audience (aud) matches the expected API identifier
- Ensures that only tokens issued for your API are accepted

### JWT Decoder

- Fetches the JWKS from AuthAction's server dynamically
- Validates the token's signature, issuer, and audience

### API Controller (`ApiController.java`)

- Public Endpoint: `/public` - No authentication required
- Protected Endpoint: `/protected` - Requires a valid JWT and extracts the sub (subject) and email from the JWT claims using `@AuthenticationPrincipal`

## Troubleshooting

### Invalid Token Errors
- Ensure the token is signed by AuthAction
- Check that the token has not expired
- Verify the token's aud and iss claims match your configuration

### Unauthorized Access (401)
This usually happens when:
- The token is missing in the request
- The token is invalid or not a Bearer token
- Spring Security is blocking the request due to misconfiguration

### JWKS Fetching or Decoding Errors
- Make sure the JWKS URI is correct: `https://your-authaction-tenant-domain/.well-known/jwks.json`
- Ensure the AuthAction domain is reachable from your app

### Audience/Issuer Mismatch
Check if your `application.properties` values match:
- `authaction.audience` = your API Identifier
- `authaction.domain` = your tenant domain
These must match the values inside your token.

## Contributing

Feel free to submit issues or pull requests if you encounter bugs or have suggestions for improvement!

