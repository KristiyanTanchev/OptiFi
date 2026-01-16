# OptiFi API

A RESTful Spring Boot application for **budgeting** and **investment portfolio tracking** with **JWT authentication** and **role-based authorization**.

## Features

### Security
- JWT-based authentication (login returns access token)
- Role-based authorization (`ROLE_USER`, `ROLE_ADMIN`)
- Resource ownership enforcement (users can access only their own data)

### Budgeting
- Categories (income/expense)
- Transactions (filtering + pagination)
- Monthly budgets (per category)
- Monthly summary reports

### Investing (Portfolio Tracker)
- Portfolios
- Trades (buy/sell)
- Positions computed from trades (quantity, avg cost)
- Optional price snapshots (if implemented)

## Tech Stack

- Java 17
- Spring Boot 3.5.7
- Spring Security (JWT)
- Spring Data JPA
- PostgreSQL (production) / H2 (dev & tests)
- Springdoc OpenAPI (Swagger UI)

---

## Getting Started

### Prerequisites
- Java 17+
- Gradle or Maven (depending on your build)
- (Optional) PostgreSQL running locally or via Docker
