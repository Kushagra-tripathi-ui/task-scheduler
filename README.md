\# Task Scheduler



A production-inspired distributed task scheduler built with Spring Boot, PostgreSQL, Kafka, Redis, and Docker.



\## Features implemented so far



\- Health check endpoint via Spring Boot Actuator

\- Task CRUD API (Create, Read, Update, Delete)

\- PostgreSQL persistence via Spring Data JPA

\- Request validation with meaningful error responses

\- Global exception handling



\## Tech Stack



\- Java 21

\- Spring Boot 3

\- Spring Data JPA

\- PostgreSQL 18

\- Maven



\## API Endpoints



| Method | Endpoint            | Description         |

|--------|---------------------|----------------------|

| POST   | /api/tasks           | Create a new task    |

| GET    | /api/tasks           | Get all tasks         |

| GET    | /api/tasks/{id}       | Get a task by ID      |

| PUT    | /api/tasks/{id}       | Update a task          |

| DELETE | /api/tasks/{id}       | Delete a task          |

| GET    | /actuator/health      | Health check           |



\## Setup



1\. Install Java 21, Maven, PostgreSQL

2\. Create database: `CREATE DATABASE task\_scheduler\_db;`

3\. Update `application.properties` with your DB credentials

4\. Run: `mvn spring-boot:run`



\## Architecture



See `docs/architecture.md` for diagrams.



\## Roadmap



\- \[x] Health check

\- \[x] Task CRUD

\- \[ ] Background worker / async task execution

\- \[ ] Delayed scheduling

\- \[ ] Retry mechanism

\- \[ ] Kafka integration

\- \[ ] Redis caching

\- \[ ] Docker + Docker Compose

\- \[ ] Monitoring (Prometheus/Grafana)

\- \[ ] Kubernetes deployment

