# 🪙 RGTS Gold v2

A distributed microservices-based trading platform built with Java, Spring Boot, Kafka, and Docker — designed to simulate real-world financial data pipelines, service orchestration, and full-stack integration.

## 🌐 Overview

**Gold v2** is a modular, production-style system comprising multiple Spring Boot services, Kafka-based messaging, and a Python API, orchestrated via Docker Compose and backed by Prometheus monitoring.

> Designed to explore service-to-service communication, async event handling, and robust infrastructure patterns in a realistic trading system.

## 🧱 Architecture

```
                 +-------------------+
                 |    Frontend       |
                 +--------+----------+
                          |
                          v
               +----------+-----------+
               |     API Gateway      |
               +----+-----------+-----+
                    |           |
        +-----------+--+     +--+------------+
        | Authentication |   | Holdings     |
        | Service         |   | Service      |
        +-----------------+   +--------------+
                    |           |
                    v           v
              Kafka Topics / Async Messaging
                    |
    +---------------+-------------------+
    |    Notification / Pricing / Email |
    |          Services (Kafka)         |
    +----------------+------------------+
                     |
           +---------v--------+
           | Config + Eureka  |
           +------------------+
```

## ⚙️ Tech Stack

- **Java 17** / Spring Boot
- **Python 3.x** (API layer)
- **Kafka** – for event-driven services
- **Docker Compose** – dev environment orchestration
- **Eureka** – service discovery
- **Spring Cloud Config** – centralized config
- **Prometheus** – service monitoring
- **SonarQube + OWASP** – code quality & security checks
- **Renovate** – automated dependency updates

## 🚀 Getting Started

### Prerequisites

- Docker & Docker Compose installed
- Java 17
- Python 3.x

### Run All Services

```bash
docker-compose up --build
```

This will spin up (WIP):
- Eureka Server
- Config Server
- Gateway
- All Spring Boot microservices
- Kafka broker
- Prometheus

### Run Python API

```bash
cd rgts-python-api
pip install -r requirements.txt
python app.py
```

## 📁 Module Breakdown

| Module | Description |
|--------|-------------|
| `rgts-authentication-service` | Manages auth and user sessions |
| `rgts-cache-manager` | Distributed cache for performance |
| `rgts-config-server` | Central config with Spring Cloud |
| `rgts-eureka-server` | Service registry (discovery) |
| `rgts-gateway-service` | Gateway for routing to services |
| `rgts-pricing-service` | Real-time pricing microservice |
| `rgts-kafka-service` | Handles Kafka topics |
| `rgts-notification-service` | Sends email alerts |
| `rgts-frontend` | Web interface |
| `rgts-python-api` | Python-based API to interact with core services |

## 🧪 Quality and Security

- `format.sh` – Code formatting
- `owasp.sh` – OWASP dependency scan
- `sonar-project.properties` – Static analysis via SonarQube
- `renovate.json` – Keeps dependencies up to date

## 🛠️ Development Scripts

```bash
./format.sh        # Auto-format source code
./owasp.sh         # Run OWASP dependency checks
./scan.sh          # Run Sonar
```

## 📜 License

MIT
