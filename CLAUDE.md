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

**Container runtime is Podman, not Docker.** Use `podman` for all container commands.

## Architecture

Hexagonal architecture (ports & adapters) with a single Maven module. Parent POM is `swim-validators` (not in this repo).

```
src/main/java/com/github/swim_developer/validator/ffice/provider/
├── domain/
│   ├── model/          # FficeMessage, ReceivedMessage, HttpResult (records/POJOs)
│   └── port/
│       ├── in/         # Inbound ports (interfaces for use cases)
│       └── out/        # Outbound ports (repository interfaces)
├── application/
│   └── usecase/        # Port implementations: ConformanceTestService, MessageService,
│                       #   SubscriptionService, FficeMessageExtractor, ConsoleService
└── infrastructure/
    ├── rest/           # JAX-RS endpoints (ProviderProxyResource, AmqpApiResource,
    │   │               #   ConformanceTestResource, MessageResource, ApiResource)
    │   └── dto/        # Response DTOs
    ├── client/         # Outbound HTTP clients with mTLS (ProviderHttpClient, ConformanceHttpClient)
    ├── messaging/      # AMQP integration via Vert.x Proton (UserReceiverLifecycle,
    │                   #   UserConnectionTracker, AmqpSslConfigurator)
    └── persistence/    # MariaDB/Hibernate persistence (ReceivedMessageRepositoryImpl)
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

- **Max 400 lines per Java file.** No inner/nested classes — every class in its own file.
- **Logging**: `@Slf4j` only. `LoggerFactory.getLogger()` is forbidden.
- **No AI co-authorship trailers** in commits. A global git hook strips them.
- **Build always with** `./mvnw clean package -DskipTests` (never `-Dmaven.test.skip=true`).
- **Integration tests require** `-DskipITs=false` (skipped by default in pom.xml).
- **Podman only** — Docker daemon is not running on this machine.
- Deployments to OpenShift/Kubernetes require explicit user confirmation before execution.
