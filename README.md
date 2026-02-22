# OptiFI

OptiFI is a personal finance tracking application built with a modern **Spring Boot + React** stack.

It allows users to manage:
- accounts
- transactions
- categories
- budgets

This repository is a **monorepo** containing both the backend and frontend.

---

## Live Demo

A live demo deployment is available for preview and evaluation purposes:

- **Frontend:** https://optifi.kvtmail.com  
- **Backend API:** https://api.optifi.kvtmail.com/api  
- **Swagger UI (OpenAPI 3):** https://api.optifi.kvtmail.com/docs

> Note: Not all backend endpoints are implemented in the frontend UI.

---

## Tech Stack

### Backend
- Java 17
- Spring Boot
- Spring Security (JWT-based authentication)
- Google OAuth2 (optional)
- JPA / Hibernate
- PostgreSQL

### Frontend
- React
- Vite
- TypeScript
- Material UI (MUI)

### Hosting
- Railway (backend and database)
- Cloudflare Pages (frontend)
- Custom domain: `optifi.kvtmail.com`

---

## Architecture

The backend follows a layered architecture:

`Controller → Service → Repository → Database`

Security is handled via Spring Security with stateless JWT authentication.
API contracts are documented using OpenAPI 3 (Swagger).

---

## Diagrams

- **App / Layered architecture:** `docs/img/app-architecture-diagram.png`
- **Database (ERD):** `docs/img/db-architecture-diagram.png`

---

## Repository Structure

```
backend/     Spring Boot API
frontend/    React application
docs/        Project documentation (diagrams + notes)
.github/     CI/CD workflows
```

---

## CI / Automation

GitHub Actions pipeline performs automated build and unit test verification on every push and pull request.

---

## Quick Start (Docker)

A Docker-based local setup is the recommended way to run the full stack.

### Prerequisites
- Docker + Docker Compose

### Run
From the repo root:

```bash
docker-compose up --build
```

### Ports
- Frontend: http://localhost:5173
- Backend: http://localhost:8080

### API Documentation (Local)

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/docs

---

## Running Locally (without Docker)

### Prerequisites
- JDK 17+
- Node.js 20+
- PostgreSQL (for dev) or H2 (for demo/test, if configured in your profiles)

### Ports
- Backend: http://localhost:8080  
- Frontend: http://localhost:5173  

Running the backend and frontend requires **two terminals**.

---

### Backend

From repo root:

```bash
cd backend
./gradlew bootRun
```

**Profiles**
- If you maintain multiple Spring profiles (e.g. `demo`, `dev`), run with:

```bash
SPRING_PROFILES_ACTIVE=demo ./gradlew bootRun
```

---

### Frontend

From repo root:

```bash
npm --prefix frontend install
npm --prefix frontend run dev
```

---

## Authentication
The backend is stateless — authentication is handled via JWT tokens.

Upon successful login, a JWT is issued and must be included in:

`Authorization: Bearer <token>` 

for accessing protected endpoints.

OptiFI supports:
- Username / Password login (JWT)
- Google OAuth2 login (optional)

If you want to enable Google OAuth locally, add your client id to the appropriate properties file (example):

```properties
security.google.client-id=YOUR_GOOGLE_CLIENT_ID
```

---

## Roles

The system supports:
- `USER`
- `ADMIN`

> Currently there are no admin-only UI features, but the role exists for future expansion.

---

## Database

PostgreSQL is used in development and production.

Schema is currently managed via `schema.sql` and Hibernate DDL auto-generation in development.

Migration tooling (e.g. Flyway) is planned.

---

## Status

This project is under active development.

Planned improvements:
- Increase unit test coverage
- Add integration tests
- Expand OpenAPI (Swagger) coverage to have better dto examples
- Introduce database migrations (Flyway)
- Achieve full frontend coverage of backend endpoints
- Admin-specific features

---

## License

MIT (see `LICENSE`)
