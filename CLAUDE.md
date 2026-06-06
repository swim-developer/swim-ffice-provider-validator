# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SWIM FF-ICE Provider Validator — a Quarkus-based conformance testing tool that validates FF-ICE (Flight and Flow Information for a Collaborative Environment) provider implementations. It acts as a test client that connects to FF-ICE providers via mTLS HTTP proxy and AMQP, captures messages, and runs conformance scenarios against the SWIM Yellow Profile specification.

Part of the `swim-developer` open-source reference architecture for ICAO SWIM services on Red Hat OpenShift.

## Build & Test Commands

```bash
# Build (compiles tests but skips execution)
./mvnw clean package -DskipTests

# Run unit tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=FficeMessageExtractorTest

# Run integration tests (requires Podman for Testcontainers)
./mvnw verify -DskipITs=false

# Dev mode with live reload
./mvnw quarkus:dev

# Start MariaDB for local dev
podman compose up -d

# JaCoCo coverage report → target/site/jacoco/index.html
./mvnw test jacoco:report
```

## Architecture

Hexagonal architecture (ports & adapters) with a single Maven module. Parent POM is `swim-validators` (not in this repo).

```
src/main/java/.../validator/ffice/provider/
├── domain/             # Model records/POJOs and port interfaces (in/out)
├── application/        # Use case implementations
└── infrastructure/
    ├── rest/           # JAX-RS endpoints and DTOs
    ├── client/         # Outbound HTTP clients with mTLS
    ├── messaging/      # AMQP integration via Vert.x Proton
    └── persistence/    # MariaDB/Hibernate persistence
```

### Key Flows

1. **mTLS Proxy** — `ProviderProxyResource` forwards REST calls (subscriptions, topics, features) to the real FF-ICE provider through `ProviderHttpClient`, which handles mutual TLS with PKCS12/JKS keystores.

2. **AMQP Message Capture** — `UserReceiverLifecycle` creates per-user Vert.x Proton receivers on provider queues, extracts FF-ICE fields via `FficeMessageExtractor` (regex-based XML parsing), and persists messages to MariaDB.

3. **Conformance Testing** — `ConformanceTestService` executes named scenarios (API-01..04, DM-01..04, WFS-01) against provider endpoints and returns pass/fail assertions per Yellow Profile requirements (REQ-0100, REQ-0110, REQ-0120, REQ-0150).

### Data Layer

- **Production/Dev**: MariaDB (compose.yml maps port 3311 → 3306, db: `swim_ffice_provider_validator`, user: `swim`)
- **Test**: H2 in-memory
- Hibernate `schema-management.strategy=update` (auto DDL)

## Code Standards

- **Integration tests require** `-DskipITs=false` (skipped by default in pom.xml).
