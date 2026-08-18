# API Gateway - Online Bookstore

Single entry point for all microservices. Client app talks ONLY to this (port 8080).

## Run
```
mvn clean spring-boot:run
```

## Current Routes
| Client calls (via Gateway) | Forwards to |
|---|---|
| http://localhost:8080/api/auth/** | http://localhost:8081/auth/** (Auth Service) |

## Next Steps
- Add routes for other microservices as they become ready
- Add JWT validation filter (Step 2)
- Add CORS config (Step 3)
- Add Rate Limiting (Step 4)
