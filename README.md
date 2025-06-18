# 🪙 RGTS Gold v2

A distributed, event-driven microservices trading platform built with **Java (Spring Boot)**, **Kafka**, **Docker**, and
**Python** — designed to simulate real-world financial systems, async workflows, and full-stack service orchestration.

---

## 🌐 Overview

**RGTS Gold v2** is a modular, production-inspired system comprising:

- Kafka-based asynchronous messaging
- Independent Spring Boot microservices
- Python-based API layer
- Docker Compose orchestration
- Observability and quality tooling

> Built to explore scalable microservice design, distributed event handling, and modern DevOps practices.

---

## 🧱 System Architecture

```
                 +-------------------+
                 |     Frontend      |
                 +--------+----------+
                          |
                          v
               +----------+-----------+
               |       API Gateway     |
               +----+-----------+-----+
                    |           |
        +-----------+--+     +--+------------+
        | Authentication |   | Holdings     |
        | Service         |   | Service      |
        +-----------------+   +--------------+
                    |           |
                    v           v
               Kafka Topics / Async Events
                    |
    +---------------+-------------------+
    | Notification / Pricing / Email   |
    |       Services via Kafka         |
    +----------------+-----------------+
                     |
           +---------v--------+
           | Config + Eureka  |
           +------------------+
```

---

## 📩 Admin Messaging with Kafka & Protobuf

A centralised [`admin.proto`](./protos/admin.proto) schema enables consistent, event-driven communication between
services using **Kafka**.

### 🧱 Message Design

Each Kafka message is wrapped in an `Envelope`, containing:

#### 🔹 Header (Metadata)

- `subject`: Event type (`USER_CREATED`, `USER_DELETED`, etc.)
- `destinationService`: Enum to route the message to the intended service
- `sourceService`: Originating microservice
- `timestamp`, `traceId`, `correlationId`: For observability & distributed tracing
- `userId`: Optional, scoped to user-specific events

#### 🔹 Payload

- A `oneof` payload block containing event-specific objects:
    - `UserCreated`, `UserDeleted`, `InvestmentCreated`, etc.

### 📦 Example `admin.proto`

```proto
enum DestinationService {
  UNKNOWN = 0;
  PRICING_SERVICE = 1;
  INVESTMENT_SERVICE = 2;
}

message Header {
  string subject = 1;
  DestinationService destinationService = 2;
  string sourceService = 3;
  string timestamp = 4;
  string correlationId = 5;
  string traceId = 6;
  string userId = 7;
}

message Envelope {
  Header header = 1;

  oneof payload {
    UserCreated userCreated = 2;
    UserDeleted userDeleted = 3;
    InvestmentCreated investmentCreated = 4;
  }
}
```

### 🔄 Routing Logic

Each service consumes from a shared Kafka topic and processes messages **only if**:

```java
header.getDestinationService().name().equalsIgnoreCase(thisServiceName)
```

This ensures **clean separation**, minimal coupling, and avoids topic bloat.

---

## ⚙️ Tech Stack

- **Java 17** / Spring Boot
- **Python 3.x**
- **Apache Kafka** (async messaging)
- **Docker Compose** (local orchestration)
- **Spring Eureka** (service discovery)
- **Spring Cloud Config** (centralized config)
- **Prometheus** (monitoring)
- **SonarQube** + **OWASP** (code quality + security)
- **Renovate** (automated dependency management)

---

## 🚀 Getting Started

### 📦 Prerequisites

- [Docker + Docker Compose](https://docs.docker.com/get-docker/)
- Java 17
- Python 3.x

### 🔧 Run Full Stack

```bash
docker-compose up --build
```

This launches:

- Eureka (discovery)
- Spring Config Server
- API Gateway
- Kafka broker
- Core microservices
- Prometheus monitoring

### 🐍 Run Python API

```bash
cd rgts-python-api
pip install -r requirements.txt
python app.py
```

---

## 📁 Module Breakdown

| Module                        | Description                           |
|-------------------------------|---------------------------------------|
| `rgts-authentication-service` | Manages auth, JWTs, and user sessions |
| `rgts-cache-manager`          | Caching layer for performance         |
| `rgts-config-server`          | Spring Cloud centralized config       |
| `rgts-eureka-server`          | Service registry via Eureka           |
| `rgts-gateway-service`        | Routes frontend/API traffic           |
| `rgts-pricing-service`        | Real-time pricing engine              |
| `rgts-kafka-service`          | Kafka setup and topic handling        |
| `rgts-notification-service`   | Email/alerts service                  |
| `rgts-frontend`               | Web UI interface                      |
| `rgts-python-api`             | External Python API gateway           |

---

## 🧪 Quality & Security

Includes tooling to enforce standards:

- `format.sh` – Code formatting
- `owasp.sh` – Vulnerability scan
- `scan.sh` – SonarQube analysis
- `renovate.json` – Dependency updates

---

## 🔧 Dev Scripts

```bash
./format.sh     # Auto-format source
./owasp.sh      # OWASP security check
./scan.sh       # Run Sonar static analysis
```

---

## 📜 License

MIT License — free to use, modify, and distribute.
