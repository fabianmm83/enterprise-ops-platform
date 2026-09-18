# Enterprise Operations Platform

> Backend empresarial para gestión de operaciones internas — usuarios, proyectos, tareas y auditoría — construido con Java 21, Spring Boot 3 y PostgreSQL.

[![CI](https://github.com/fabianmm83/enterprise-ops-platform/actions/workflows/ci.yml/badge.svg)](https://github.com/fabianmm83/enterprise-ops-platform/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)](https://www.docker.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## Tabla de contenido

- [Descripción](#descripción)
- [Motivación](#motivación)
- [Stack técnico](#stack-técnico)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [Seguridad](#seguridad)
- [Endpoints de la API](#endpoints-de-la-api)
- [Prácticas de ingeniería](#prácticas-de-ingeniería)
- [Testing](#testing)
- [Cómo ejecutar el proyecto](#cómo-ejecutar-el-proyecto)
- [Deployment](#deployment)
- [Roadmap](#roadmap)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Decisiones técnicas](#decisiones-técnicas)
- [Autor](#autor)
- [Licencia](#licencia)

---

## Descripción

**Enterprise Operations Platform** es un backend empresarial diseñado para centralizar la gestión de operaciones internas de una organización: usuarios, roles, proyectos, tareas y auditoría de acciones.

El proyecto implementa un modelo de **control de acceso basado en roles (RBAC)** con tres niveles de permisos (`ADMIN`, `MANAGER`, `USER`), un sistema de **auditoría completo** de las acciones críticas, **autenticación con JWT**, y está pensado para demostrar prácticas de ingeniería de software aplicadas en entornos empresariales reales.

### Alcance

**Incluye:**
- Gestión de usuarios con roles y permisos
- CRUD de proyectos con estados y asignaciones
- Gestión de tareas con prioridades, estados y fechas
- Audit log de acciones críticas
- Autenticación JWT con refresh tokens
- Documentación automática con OpenAPI/Swagger
- Testing unitario e integración
- Docker + CI/CD + Deploy

**No incluye (fuera del alcance actual):**
- Frontend web (el proyecto se enfoca exclusivamente en backend)
- Notificaciones por email
- Integración con servicios de terceros (Slack, Jira, etc.)
- Reportes avanzados o analytics

---

## Motivación

Este proyecto nace como una pieza central de portafolio profesional para demostrar competencias en:

- Desarrollo backend con **Java + Spring Boot** en un contexto empresarial
- Diseño de **APIs REST** con buenas prácticas (versionado, paginación, filtros, códigos de estado)
- **Seguridad** con JWT, RBAC y hashing de contraseñas
- **Persistencia** con PostgreSQL, JPA y migraciones versionadas
- **Testing** automatizado con JUnit 5, Mockito y Testcontainers
- **DevOps** con Docker, GitHub Actions y despliegue en GCP Cloud Run
- **Arquitectura limpia** por capas con separación de responsabilidades

---

## Stack técnico

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.3**
- **Spring Security** (JWT)
- **Spring Data JPA**
- **Bean Validation** (Jakarta)
- **Flyway** (migraciones)
- **Lombok** (boilerplate)

### Base de datos
- **PostgreSQL 16**
- **HikariCP** (connection pool)

### Testing
- **JUnit 5**
- **Mockito**
- **Testcontainers** (PostgreSQL)
- **Spring Boot Test**
- **AssertJ**

### Documentación
- **springdoc-openapi** (Swagger UI)

### Build & DevOps
- **Maven 3.9+**
- **Docker** (multi-stage build)
- **Docker Compose** (desarrollo local)
- **GitHub Actions** (CI/CD)

### Deploy
- **GCP Cloud Run** (backend)
- **Firebase Hosting** (documentación estática)
- **Neon Postgres** (base de datos gestionada)

---

## Arquitectura

El proyecto sigue una **arquitectura por capas** (layered architecture) que separa responsabilidades y facilita el testing:
┌─────────────────────────────────────────────┐
│ Controllers (REST API) │
│ Reciben requests HTTP, validan input │
│ Delegan a servicios │
└────────────────────┬────────────────────────┘
│
▼
┌─────────────────────────────────────────────┐
│ Services (Lógica de negocio) │
│ Reglas de negocio, transacciones, │
│ validaciones de dominio │
└────────────────────┬────────────────────────┘
│
▼
┌─────────────────────────────────────────────┐
│ Repositories (Acceso a datos) │
│ Spring Data JPA, queries personalizadas │
└────────────────────┬────────────────────────┘
│
▼
┌─────────────────────────────────────────────┐
│ PostgreSQL │
└─────────────────────────────────────────────┘

text

### Capas transversales
- **Security** — Filtros JWT, configuración de Spring Security, RBAC
- **Exceptions** — Manejo global de errores con `@ControllerAdvice`
- **DTOs** — Separación entre entidades y contratos de API
- **Mappers** — Conversión entre entidades y DTOs
- **Audit** — Registro automático de acciones críticas

---

## Modelo de datos

### Entidades principales

**User**
| Campo | Tipo | Notas |
|---|---|---|
| id | UUID | Primary key |
| email | String | Unique, not null |
| password | String | BCrypt hashed |
| fullName | String | Not null |
| role | Enum | ADMIN, MANAGER, USER |
| active | Boolean | Default true |
| createdAt | Timestamp | Auto |
| updatedAt | Timestamp | Auto |

**Role** — Enum con tres valores: `ADMIN`, `MANAGER`, `USER`

**Project**
| Campo | Tipo | Notas |
|---|---|---|
| id | UUID | Primary key |
| name | String | Not null |
| description | Text | |
| status | Enum | ACTIVE, PAUSED, COMPLETED, ARCHIVED |
| owner | User | FK |
| startDate | Date | |
| endDate | Date | Nullable |
| createdAt | Timestamp | Auto |
| updatedAt | Timestamp | Auto |

**Task**
| Campo | Tipo | Notas |
|---|---|---|
| id | UUID | Primary key |
| title | String | Not null |
| description | Text | |
| project | Project | FK |
| assignee | User | FK, nullable |
| status | Enum | TODO, IN_PROGRESS, REVIEW, DONE |
| priority | Enum | LOW, MEDIUM, HIGH, URGENT |
| dueDate | Date | Nullable |
| createdAt | Timestamp | Auto |
| updatedAt | Timestamp | Auto |

**AuditLog**
| Campo | Tipo | Notas |
|---|---|---|
| id | UUID | Primary key |
| user | User | FK |
| action | String | LOGIN, CREATE_PROJECT, etc. |
| entityType | String | PROJECT, TASK, USER |
| entityId | UUID | ID de la entidad afectada |
| ipAddress | String | |
| timestamp | Timestamp | Auto |

### Relaciones
User ────< Project (owner)
User ────< Task (assignee)
Project ──< Task
User ────< AuditLog

text

---

## Seguridad

### Autenticación
- **JWT** (JSON Web Tokens) con firma HS512
- Access token con expiración de **15 minutos**
- Refresh token con expiración de **7 días**
- Contraseñas hasheadas con **BCrypt** (strength 12)

### Autorización (RBAC)

| Recurso | USER | MANAGER | ADMIN |
|---|---|---|---|
| Ver sus propias tareas | ✅ | ✅ | ✅ |
| Ver todas las tareas | ❌ | ✅ | ✅ |
| Crear tareas | ❌ | ✅ | ✅ |
| Asignar tareas a otros | ❌ | ✅ | ✅ |
| Crear proyectos | ❌ | ✅ | ✅ |
| Editar cualquier proyecto | ❌ | ✅ | ✅ |
| Eliminar proyectos | ❌ | ❌ | ✅ |
| Gestionar usuarios | ❌ | ❌ | ✅ |
| Ver audit log | ❌ | ❌ | ✅ |

Implementado con:
- `@PreAuthorize` en métodos de servicio
- Filtros de Spring Security
- `SecurityContext` con `UserDetails` personalizado

### Buenas prácticas aplicadas
- Nunca exponer contraseñas en respuestas
- Validación de JWT en cada request protegido
- Rate limiting básico en endpoints de autenticación
- CORS configurado explícitamente
- Headers de seguridad (HSTS, X-Content-Type-Options)

---

## Endpoints de la API

Base URL: `/api/v1`

### Autenticación

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/auth/register` | Registrar nuevo usuario | No |
| POST | `/auth/login` | Iniciar sesión | No |
| POST | `/auth/refresh` | Renovar access token | Refresh token |
| POST | `/auth/logout` | Cerrar sesión | Sí |

### Usuarios

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| GET | `/users` | Listar usuarios (paginado) | ADMIN |
| GET | `/users/{id}` | Ver usuario por ID | ADMIN |
| POST | `/users` | Crear usuario | ADMIN |
| PUT | `/users/{id}` | Actualizar usuario | ADMIN |
| DELETE | `/users/{id}` | Eliminar usuario | ADMIN |
| GET | `/users/me` | Ver perfil propio | Autenticado |

### Proyectos

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| GET | `/projects` | Listar proyectos (paginado, filtros) | MANAGER+ |
| GET | `/projects/{id}` | Ver proyecto | MANAGER+ |
| POST | `/projects` | Crear proyecto | MANAGER+ |
| PUT | `/projects/{id}` | Actualizar proyecto | MANAGER+ |
| DELETE | `/projects/{id}` | Eliminar proyecto | ADMIN |

### Tareas

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| GET | `/tasks` | Listar tareas (filtros: status, priority, assignee) | Autenticado |
| GET | `/tasks/{id}` | Ver tarea | Autenticado |
| POST | `/tasks` | Crear tarea | MANAGER+ |
| PUT | `/tasks/{id}` | Actualizar tarea | MANAGER+ |
| PATCH | `/tasks/{id}/status` | Cambiar status | Assignee o MANAGER+ |
| DELETE | `/tasks/{id}` | Eliminar tarea | ADMIN |

### Auditoría

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| GET | `/audit-logs` | Listar registros (paginado) | ADMIN |

### Convenciones

- **Versionado:** `/api/v1/...`
- **Paginación:** `?page=0&size=20&sort=createdAt,desc`
- **Filtros:** `?status=ACTIVE&priority=HIGH`
- **Errores:** formato estándar con `timestamp`, `status`, `error`, `message`, `path`

---

## Prácticas de ingeniería

Este proyecto aplica las siguientes prácticas profesionales:

### Diseño de software
- **Arquitectura por capas** con separación estricta de responsabilidades
- **DTOs** para desacoplar entidades JPA de contratos de API
- **Mappers** dedicados para conversiones
- **Inyección de dependencias** por constructor
- **Programación defensiva** con validaciones en múltiples niveles

### APIs REST
- Convenciones REST: sustantivos en plural, verbos HTTP correctos
- Códigos de estado HTTP semánticos (200, 201, 204, 400, 401, 403, 404, 409)
- Versionado desde el día 1
- Paginación y filtros en endpoints de listado
- Documentación con OpenAPI 3

### Seguridad
- JWT con access + refresh tokens
- BCrypt para hashing de contraseñas
- RBAC con `@PreAuthorize`
- Validación de input con Bean Validation
- CORS explícito
- Rate limiting básico

### Persistencia
- Migraciones versionadas con Flyway
- Índices en columnas de búsqueda frecuente
- Constraints a nivel de base de datos
- Transacciones declarativas con `@Transactional`

### Calidad de código
- Cobertura de tests > 70%
- Sin warnings de compilación
- Logs estructurados
- Código formateado consistentemente
- Commits siguiendo Conventional Commits

### DevOps
- Docker multi-stage (imagen final < 200 MB)
- Docker Compose para desarrollo local
- CI/CD con GitHub Actions
- Análisis estático con SonarCloud (opcional)
- Deploy automatizado a Cloud Run

---

## Testing

### Estrategia

| Tipo | Herramienta | Cobertura |
|---|---|---|
| **Unit tests** | JUnit 5 + Mockito | Servicios y utilidades |
| **Integration tests** | Spring Boot Test + Testcontainers | Repositorios y controllers |
| **End-to-end** | RestAssured (opcional) | Flujos completos |

### Objetivos de cobertura

- **Servicios:** > 80%
- **Controllers:** > 70%
- **Repositorios:** > 60% (con Testcontainers)
- **Cobertura global:** > 70%

### Ejecutar tests

```bash
# Todos los tests
./mvnw test

# Solo unit tests
./mvnw test -Dtest="*Test"

# Solo integration tests
./mvnw test -Dtest="*IT"

# Con reporte de cobertura
./mvnw verify jacoco:report
Cómo ejecutar el proyecto
Requisitos previos
Java 21 o superior (Temurin)

Maven 3.9+ o usar el wrapper incluido (./mvnw)

Docker Desktop (descargar)

PostgreSQL 16 (o usar el contenedor incluido en Docker Compose)

Ejecución local con Docker Compose
bash
# 1. Clonar el repo
git clone https://github.com/fabianmm83/enterprise-ops-platform.git
cd enterprise-ops-platform

# 2. Levantar PostgreSQL con Docker
docker compose up -d postgres

# 3. Ejecutar la aplicación
./mvnw spring-boot:run

# La API estará disponible en http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
Variables de entorno
Crea un archivo .env en la raíz (basado en .env.example):

env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=enterprise_ops
DB_USER=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=your-256-bit-secret-key-here-change-in-production
JWT_ACCESS_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
Deployment
Arquitectura de producción
text
┌──────────────────────────────────────────┐
│  Firebase Hosting                        │
│  enterprise-ops.web.app                  │
│  (documentación + Swagger UI)            │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│  GCP Cloud Run                           │
│  api-enterprise-ops-xxxxx.run.app        │
│  (backend Spring Boot en Docker)         │
└────────────────┬─────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────┐
│  Neon PostgreSQL (free tier)             │
│  Base de datos gestionada                │
└──────────────────────────────────────────┘
Pasos de deploy
bash
# 1. Build de la imagen Docker
docker build -t enterprise-ops-platform .

# 2. Tag para GCP
docker tag enterprise-ops-platform gcr.io/PROJECT_ID/enterprise-ops-platform

# 3. Push a Container Registry
docker push gcr.io/PROJECT_ID/enterprise-ops-platform

# 4. Deploy a Cloud Run
gcloud run deploy enterprise-ops-platform \
  --image gcr.io/PROJECT_ID/enterprise-ops-platform \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod"
El pipeline de GitHub Actions automatiza estos pasos en cada push a main.

Roadmap
Fase 1 — Setup y autenticación ✅ (Semana 1)
☑ Estructura del proyecto Maven
☑ Configuración de Spring Boot
☑ PostgreSQL con Docker Compose
☑ Entidades User y Role
☑ Spring Security + JWT
☑ Endpoints /auth/register, /auth/login, /auth/refresh
Fase 2 — CRUD y lógica de negocio (Semana 2)
□ CRUD completo de User
□ CRUD completo de Project
□ CRUD completo de Task
□ RBAC con @PreAuthorize
□ Validaciones con Bean Validation
□ Manejo global de excepciones
□ Migraciones con Flyway
Fase 3 — Testing y documentación (Semana 3)
□ Unit tests de servicios con JUnit + Mockito
□ Integration tests con Testcontainers
□ Documentación con Swagger/OpenAPI
□ Cobertura de tests > 70%
□ Configuración de JaCoCo
Fase 4 — DevOps y deploy (Semana 4)
□ Dockerfile multi-stage
□ Docker Compose para producción
□ GitHub Actions: build + tests
□ GitHub Actions: deploy a Cloud Run
□ Base de datos en Neon
□ Documentación en Firebase Hosting
Fase 5 — Refinamiento (Semana 5-6)
□ Audit log completo
□ Rate limiting en endpoints de auth
□ Métricas con Actuator
□ Health checks
□ Documentación final
□ Link desde portafolio principal
Estructura del proyecto
text
enterprise-ops-platform/
├── .github/
│   └── workflows/
│       ├── ci.yml                 # Build + tests en cada push
│       └── deploy.yml             # Deploy a Cloud Run en main
├── docs/
│   ├── architecture.md            # Diagramas y decisiones
│   ├── api.md                     # Referencia detallada de API
│   └── decisions/                 # ADRs
├── src/
│   ├── main/
│   │   ├── java/com/torotech/enterpriseops/
│   │   │   ├── EnterpriseOpsApplication.java
│   │   │   ├── config/            # Configuraciones
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── JacksonConfig.java
│   │   │   ├── controller/        # Endpoints REST
│   │   │   ├── service/           # Lógica de negocio
│   │   │   ├── repository/        # Repositorios JPA
│   │   │   ├── entity/            # Entidades JPA
│   │   │   ├── dto/               # Data Transfer Objects
│   │   │   ├── mapper/            # Conversiones entidad ↔ DTO
│   │   │   ├── exception/         # Excepciones custom + handler
│   │   │   ├── security/          # JWT, filtros, UserDetails
│   │   │   └── audit/             # Sistema de auditoría
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/      # Migraciones Flyway
│   └── test/
│       └── java/com/torotech/enterpriseops/
│           ├── unit/              # Tests unitarios
│           ├── integration/       # Tests de integración
│           └── fixtures/          # Datos de prueba
├── .env.example
├── .gitignore
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── LICENSE
Decisiones técnicas
¿Por qué Java 21?
Última versión LTS, soporte a largo plazo (2029+). Trae mejoras de lenguaje como virtual threads, pattern matching, records, que mejoran la expresividad y el performance.

¿Por qué Spring Boot 3.3?
Framework estándar de facto para backend en Java. Ecosistema maduro, integración con Spring Security, Data JPA, y soporte nativo para Jakarta EE 10.

¿Por qué PostgreSQL?
Base de datos relacional robusta, con soporte para JSONB, particionamiento, y extensiones útiles (UUID, full-text search). Es el estándar para aplicaciones empresariales.

¿Por qué JWT?
Autenticación stateless ideal para APIs REST. Permite escalar horizontalmente sin sesiones compartidas. Access + refresh tokens balancean seguridad y UX.

¿Por qué Flyway?
Migraciones versionadas en el código, control de cambios en base de datos, reproducible en cualquier entorno.

¿Por qué Testcontainers?
Tests de integración con PostgreSQL real, no mocks. Detectan problemas que los tests unitarios no ven.

¿Por qué GCP Cloud Run?
Serverless con containers, escala a cero, tier gratis generoso. Ideal para portafolios y aplicaciones de bajo tráfico.

¿Por qué Firebase Hosting?
Hosting estático gratuito, CDN global, SSL automático. Perfecto para servir Swagger UI y documentación.

Autor
Fabian Moreno Monroy
Full Stack Engineer · Estudiante de Ingeniería Mecatrónica (UNAM)

🌐 Portafolio

💼 LinkedIn

💻 GitHub

📧 Email

Licencia
Este proyecto está bajo la Licencia MIT.

Agradecimientos
Proyecto desarrollado como pieza central de portafolio profesional, con foco en demostrar prácticas de ingeniería de software aplicadas en entornos empresariales reales.

text

---

## 2) `.gitignore` para Java/Maven

Crea el archivo `.gitignore` en la raíz del proyecto con este contenido:
============ Java ============
*.class
*.jar
*.war
*.ear
.nar
.log
hs_err_pid
replay_pid

============ Maven ============
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

============ IDE ============
.idea/
*.iml
*.iws
*.ipr
.vscode/
.settings/
.classpath
.project
.factorypath

============ Environment ============
.env
.env.local
.env.*.local
*.env

============ Logs ============
logs/
*.log

============ OS ============
.DS_Store
Thumbs.db
desktop.ini

============ Docker ============
docker-compose.override.yml

============ GCP ============
*.json
!package.json
!package-lock.json
gcp-credentials.json
service-account.json

============ Testcontainers ============
.testcontainers/