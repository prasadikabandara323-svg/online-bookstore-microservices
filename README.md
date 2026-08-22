# Gayel Online Book Store — Microservices System

A distributed, containerized microservices system built for the **Service-Oriented Computing** coursework. The system is composed of four independently owned Spring Boot microservices, a central API Gateway, and a unified client application, all orchestrated with Docker Compose.

## 1. Architecture Overview

```
                                   ┌─────────────────────┐
                                   │      Client App      │
                                   │ (HTML/JS, per-service │
                                   │   pages, port 3000/   │
                                   │   3001/8086/8087)     │
                                   └──────────┬───────────┘
                                              │ HTTP
                                              ▼
                                   ┌─────────────────────┐
                                   │     API Gateway       │
                                   │   (Spring Cloud        │
                                   │  Gateway, port 8080)   │
                                   │                        │
                                   │  • JWT verification     │
                                   │  • CORS                 │
                                   │  • Rate limiting         │
                                   │  • Injects X-API-KEY     │
                                   │    for downstream calls  │
                                   └───┬────┬────┬────┬─────┘
                  /api/auth/           │    │    │    │  /api/payments/
        ┌─────────────────────────────┘     │    │    └─────────────────────┐
        │               /api/books/   ──────┘    └───────  /api/orders/  │
        │                    │                                 & /api/cart/ │
        ▼                    ▼                            ▼            ▼
┌───────────────┐   ┌───────────────────┐   ┌───────────────┐   ┌────────────────┐
│ user-auth-     │   │ book-catalog-      │   │ order-service │   │ payment-service │
│ service        │   │ service            │   │               │   │                 │
│ (port 8081)    │   │ (port 8082)        │   │ (port 8083)   │   │ (port 8084)     │
│                │   │                    │   │               │   │                 │
│ Auth, JWT      │   │ Book CRUD          │   │ Cart & Orders │   │ Payments        │
│ issuance,      │   │                    │   │               │   │                 │
│ OTP-based      │   │                    │   │               │   │                 │
│ password reset │   │                    │   │               │   │                 │
└───────┬────────┘   └─────────┬──────────┘   └──────┬────────┘   └───────┬─────────┘
        │                       │                      │                    │
        └───────────────────────┴──────────────────────┴────────────────────┘
                                              │
                                              ▼
                                     ┌──────────────── ┐
                                     │   MongoDB 7     │
                                     │  (port 27017)   │
                                     │ shared database,│
                                     │ per-service     │
                                     │ collections     │
                                     └──────────────── ┘


Each microservice owns its own collections inside a shared MongoDB instance (bookstore_db) and enforces its own **API Key** check independently of the Gateway, so direct calls (bypassing the Gateway) are still protected.

## 2. Prerequisites

- Docker & Docker Compose
- Ports `27017`, `8080`–`8088`, `3000`, `3001`, `9090`, `9091` free on your machine
- (Optional, for local dev without Docker) Java 21+, Maven 3.9+

## 3. Running the System

From the project root (where `docker-compose.yml` lives):

docker compose up -d


This builds and starts every service. To rebuild after a code change:


docker compose up -d --build <service-name>


To check status and logs:


docker ps
docker compose logs -f <service-name>


To stop everything:

docker compose down


## 4. Services, Ports & Client URLs

- **API Gateway** — container port `8080` — no Swagger UI of its own (routes to each service's docs) — no client app of its own
- **User & Auth Service** — container port `8081` — Swagger UI: `http://localhost:9091` — Client app: `http://localhost:3001`
- **Book Catalog Service** — container port `8082` — Swagger UI: `http://localhost:9090` — Client app: `http://localhost:8087`
- **Order Service (Cart & Orders)** — container port `8083` — Swagger UI: `http://localhost:8085` — Client app: `http://localhost:3000`
- **Payment Service** — container port `8084` — Swagger UI: `http://localhost:8088` — Client app: `http://localhost:8086`
- **MongoDB** — port `27017` — no Swagger UI / client app

All of the above are also reachable through the **API Gateway** at `http://localhost:8080/api/<service-path>/...` (see routing list below).

## 5. API Gateway — Routing & Security

The Gateway (`api-gateway`, Spring Cloud Gateway, reactive/WebFlux) is the single entry point for the client app and implements:

- **JWT-based Authentication** — validates the `Authorization: Bearer < Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJheW9kaHlhcmFuYXRodW5nZUBnbWFpbC5jb20iLCJyb2xlIjoiVVNFUiIsInVzZXJJZCI6IjZhODdmYjFjMDI3MGYwOGQ3N2M1YjUwMyIsImlhdCI6MTc4NzI5NjcxOCwiZXhwIjoxNzg3MzgzMTE4fQ.Fb7Aut0f5aLFWXPWXvTKF2I6031ChVznvrlwOpo6WUT8QJHUpiYUgI5JN2YD1HMdxLI_ymw7q2X6EmA-qN3JZg >` header on protected routes before forwarding the request downstream.

- **CORS** — allows requests from the client app's origin (`http://localhost:3001` / `127.0.0.1:3001`; extend `CorsConfig.java` if you add more client origins).
- **Rate Limiting** — a simple in-memory, fixed-window limiter: max **20 requests per IP per 60 seconds**, returns `429 Too Many Requests` when exceeded.

- **API Key injection** — automatically attaches `X-API-KEY: YOUR_SECRET_API_KEY_HERE` to every downstream request, so the client app does **not** need to send it manually when going through the Gateway.

**Routing:**

- `http://localhost:8080/api/auth/login` → prefix stripped → forwarded to user-auth-service (`:8081`)
- `http://localhost:8080/api/books/**` → prefix stripped → forwarded to book-catalog-service (`:8082`)
- `http://localhost:8080/api/orders/**` and `/api/cart/**` → same path, no prefix stripped → forwarded to order-service (`:8083`)
- `http://localhost:8080/api/payments/created**` → prefix stripped → forwarded to payment-service (`:8084`)

## 6. API Key Header Format & Test Credentials

Every microservice independently enforces an API Key filter on incoming requests.

**Header format (required on all direct, non-Gateway calls):**


X-API-KEY: YOUR_SECRET_API_KEY_HERE


> When calling through the API Gateway (`localhost:8080`), this header is added automatically — you don't need to send it yourself.

**Test login credentials** — register your own via `POST /auth/register` first (no seeded accounts are shipped with the repo for security). Example request body:

json
{
  "fullName": "ayodhya",
  "email": "ayodhyaranathunge@gmail.com",
  "password": "AYO@2002apuu"
}


Then log in via `POST /auth/login` with the same `email`/`password` to receive a JWT for the `Authorization: Bearer <token>` header required by protected endpoints.

## 7. Microservice Endpoints

### 7.1 User & Auth Service (`user-auth-service`, port 8081) — Owner: [ITBNM-2313-0056]
Base path: `/auth`

- `POST /auth/register` — Register a new USER account — requires API Key
- `POST /auth/register-admin` — Register a new ADMIN account — requires API Key + ADMIN JWT
- `POST /auth/login` — Log in, returns a JWT — requires API Key
- `GET /auth/me` — Get current logged-in user's identity — requires API Key + JWT
- `POST /auth/forgot-password` — Request a password reset OTP via email — requires API Key
- `POST /auth/verify-otp` — Verify the OTP before allowing a password reset — requires API Key
- `POST /auth/reset-password` — Reset password using a verified OTP — requires API Key
- `PUT /auth/profile` — Update the logged-in user's name/photo — requires API Key + JWT
- `DELETE /auth/profile` — Permanently delete the logged-in user's own account — requires API Key + JWT
- `GET /admin/dashboard` — Admin-only test endpoint — requires API Key + ADMIN JWT

### 7.2 Book Catalog Service (`book-catalog-service`, port 8082) — Owner: [ITBNM-2313-0006]

Base path: `/books`

- `GET /books` — List all books — requires API Key
- `GET /books/{id}` — Get a single book by ID — requires API Key
- `POST /books` — Create a new book — requires API Key
- `PUT /books/{id}` — Update an existing book — requires API Key
- `DELETE /books/{id}` — Delete a book — requires API Key

### 7.3 Order Service — Cart & Orders (`order-service`, port 8083) — Owner: [ITBNM-2313-0002]

Base paths: `/api/cart`, `/api/orders`

- `GET /api/cart/{userId}` — Get a user's cart — requires API Key
- `POST /api/cart/add` — Add an item to the cart — requires API Key
- `PUT /api/cart/update` — Update an item's quantity — requires API Key
- `DELETE /api/cart/remove` — Remove an item from the cart — requires API Key
- `DELETE /api/cart/clear/{userId}` — Clear a user's entire cart — requires API Key
- `GET /api/orders/test` — Health-check endpoint — requires API Key
- `POST /api/orders/process` — Process a cart into a placed order — requires API Key
- `POST /api/orders` — Create an order directly — requires API Key
- `GET /api/orders` — List all orders — requires API Key
- `GET /api/orders/{id}` — Get an order by ID — requires API Key
- `GET /api/orders/user/{userId}` — Get all orders for a user — requires API Key
- `PUT /api/orders/{id}` — Update an order — requires API Key
- `DELETE /api/orders/{id}` — Delete an order — requires API Key

### 7.4 Payment Service (`payment-service`, port 8084) — Owner: [ITBNM-2313-0036]

Base path: `/payments`

- `POST /payments/create` or `/payments/process` — Create/process a payment for an order — requires API Key
- `GET /payments/order/{orderId}` — Get payment(s) for an order — requires API Key
- `GET /payments/method/{paymentMethod}` — Get payments filtered by payment method — requires API Key
- `PUT /payments/update-status/{id}` — Update a payment's status — requires API Key
- `DELETE /payments/{id}` — Delete a payment record — requires API Key

## 8. Containerization

Every Spring Boot service, the API Gateway, MongoDB, the Swagger UI documentation containers, and the client app pages are containerized and orchestrated from the single root `docker-compose.yml`. Each backend service also ships its own `Dockerfile`.

## 9. Team

- **_[ITBNM-2313-0056]_** — Gateway Lead — API Gateway — JWT verification, CORS, rate limiting
- **_[ITBNM-2313-0056]_** — Member1 — User & Auth Service — `/auth/register`, `/auth/login`, `/auth/profile`
- **_[ITBNM-2313-0006]_** — Member2 — Book Catalog Service — `/books`, `/books/{id}`
- **_[ITBNM-2313-0002]_** — Member3 — Order Service — `/api/cart/**`, `/api/orders/**`
- **_[ITBNM-2313-0036]_** — Member4 — Payment Service — `/payments/create`, `/payments/order/{orderId}`

member1: R.M.Ayodhya Poojani (ITBNM-2313-0056)
member2: Prasadika Bandara (ITBNM-2313-0006)
member3: Malsha Amarathilaka (ITBNM-2313-0002)
member4: Prabudi Kalindi (ITBNM-2313-0036)