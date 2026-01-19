## Demo Deployment

A live demo deployment is available for preview and evaluation purposes.

- **Frontend:** https://optifi.kvtmail.com
- **Backend API:** https://api.optifi.kvtmail.com/api

> The demo environment is intended for evaluation only.  
> Data may be reset at any time and no uptime guarantees are provided.

---

# OptiFI

OptiFI is a personal finance tracking application built with a modern Java + React stack.

It allows users to manage:
- accounts
- transactions
- categories
- authentication with JWT

The project is split into a backend (Spring Boot) and a frontend (React + Vite).

---

## Tech Stack

### Backend
- Java
- Spring Boot
- Spring Security (JWT)
- JPA / Hibernate
- MariaDB, PostgreSQL

### Frontend
- React
- Vite
- TypeScript
- Material UI (MUI)
- React Router
- TanStack React Query

---

## Running locally

### Prerequisites
- **JDK 17+**
- **Node.js 18+**
- **MariaDB** (or compatible database)

> In development, the database schema is **auto-created** by the backend.  
> No manual SQL setup is required.

---

### Ports
- Backend: http://localhost:8080
- Frontend: http://localhost:5173

Running the backend and frontend requires **two terminals**.

---

### Backend

> Requires a running database and valid datasource configuration.

Windows (PowerShell):
```bash
cd backend; .\gradlew.bat bootRun
```
```bash
# Linux / macOS:
cd backend && ./gradlew bootRun
```
The backend runs until stopped with Ctrl+C.

---

### Frontend
From repo root (recommended, works everywhere):
```bash
npm --prefix frontend install
npm --prefix frontend run dev
```

---

## Status
This project is under active development and currently represents an MVP.