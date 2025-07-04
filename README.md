# 🪙 RGTS Gold v2
This is a **rebuild** of the original [RGTS Gold project](https://github.com/ItsRahim/gold-tracker-project), focusing on modularity, scalability, and maintainability.

A distributed, event-driven microservices trading platform built with **Java (Spring Boot)**, **Kafka**, **Docker**, and
**Python** — designed to track live gold prices and track user investments

---

## 🌐 Overview

**RGTS Gold v2** is a modular, production-inspired system comprising:

- Kafka-based asynchronous messaging
- Independent Spring Boot microservices
- Python-based API layer
- Docker Compose orchestration
- Observability and quality tooling
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
                          |
                  +-------+---------+  
                  | Authentication  | 
                  |     Service     |
                  +-----------------+
                    |           |
                    v           v
               Kafka Topics / Async Events
                          |
          +---------------+------------------+
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

A centralised [`admin.proto`] schema enables consistent, event-driven communication between services using **Kafka**.

### 🧱 Message Design

Each Kafka message is wrapped in an `Envelope`, containing:

#### 🔹 Header (Metadata)

- `eventId`: Unique identifier for the event (UUID)
- `eventType`: Event type (e.g., `user.created`, `user.deleted`)
- `occurredAt`: Timestamp when the event occurred
- `aggregateId`: Identifier of the aggregate root related to the event (often the `userId`)
- `correlationId`: Used for tracing the flow across services
- `causationId`: ID of the event or action that caused this event

#### 🔹 Payload

- A `oneof` payload block containing event-specific objects:
    - `UserCreated`, `UserDeleted`, etc.

### 📦 Example `admin.proto`

```proto
package com.rahim.proto.protobuf.admin;

message UserCreated {
  required string userId = 1;
  required string email = 2;
}

message UserDeleted {
  required string userId = 1;
}

message Metadata {
  required string eventId = 1;
  required string eventType = 2;
  required string occurredAt = 3;
  optional string aggregateId = 4;
  required string correlationId = 5;
  required string causationId = 6;
}

message Envelope {
  required Metadata metadata = 1;
  oneof payload {
    UserCreated userCreated = 2;
    UserDeleted userDeleted = 3;
  }
}
```

### 🔄 Routing Logic

All microservices consume from a shared Kafka topic and decide whether to process a message by checking 
the payload type inside the `Envelope`. Each service processes messages only if the payload matches the events it handles.

```java
Envelope envelope = Envelope.parseFrom(kafkaMessage.getValue());

switch (envelope.getPayloadCase()) {
    case USERCREATED:
        if (thisServiceHandlesUserEvents) {
            // Process UserCreated event
        }
        break;
    default:
        break;
}
```
---

## ⚙️ Tech Stack

- **Java 21** / Spring Boot
- **Python 3.x**
- **Apache Kafka** (async messaging)
- **Docker Compose** (local orchestration)
- **Spring Eureka** (service discovery)
- **Spring Cloud Config** (centralized config)
- **Prometheus** (monitoring)
- **SonarQube** + **OWASP** (code quality + security)
- **Renovate** (automated dependency management)
- **MinIO** / **AWS S3** (object storage)
- **Protobuf** (message serialization)
- **React** (frontend UI)

---

## 🚀 Getting Started

### 📦 Prerequisites

- [Docker + Docker Compose](https://docs.docker.com/get-docker/)
- Java 21
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
./startup.sh
```

---

## 📁 Module Breakdown

| Module                        | Description                           |
|-------------------------------|---------------------------------------|
| `rgts-authentication-service` | Manages auth, JWTs, and user sessions |
| `rgts-cache-manager`          | Caching layer for performance         |
| `rgts-config-server`          | Spring Cloud centralised config       |
| `rgts-email-service`          | Sends out emails to users             |
| `rgts-eureka-server`          | Service registry via Eureka           |
| `rgts-frontend`               | Web UI interface                      |
| `rgts-gateway-service`        | Routes frontend/API traffic           |
| `rgts-kafka-service`          | Kafka setup and topic handling        |
| `rgts-pricing-service`        | Real-time pricing engine              |
| `rgts-python-api`             | Web scraper to fetch price updates    |
| `rgts-storage-service`        | Manages MinIO/AWS S3 Bucket storage   |

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
```

---

## 📜 License

MIT License — free to use, modify, and distribute.
