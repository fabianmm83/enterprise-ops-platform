# Enterprise Operations Platform

> Backend empresarial para gestión de operaciones internas — usuarios, proyectos, tareas y auditoría — construido con Java 21, Spring Boot 4 y PostgreSQL, desplegado en producción.

[![Deploy to Cloud Run](https://github.com/fabianmm83/enterprise-ops-platform/actions/workflows/deploy.yml/badge.svg)](https://github.com/fabianmm83/enterprise-ops-platform/actions/workflows/deploy.yml)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)](https://www.docker.com/)
[![GCP Cloud Run](https://img.shields.io/badge/GCP-Cloud_Run-4285F4?logo=googlecloud)](https://cloud.google.com/run)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**🔗 Links rápidos:**
- **API en producción:** https://enterprise-ops-api-931400252050.us-central1.run.app
- **Swagger UI:** https://enterprise-ops-api-931400252050.us-central1.run.app/swagger-ui.html
- **Landing del proyecto:** https://fabianmmcv.web.app/enterprise-ops

---

## Tabla de contenido

- [Descripción](#descripción)
- [Estado del proyecto](#estado-del-proyecto)
- [Stack técnico](#stack-técnico)
- [Arquitectura](#arquitectura)
- [Modelo de datos](#modelo-de-datos)
- [Seguridad](#seguridad)
- [Endpoints de la API](#endpoints-de-la-api)
- [Prácticas de ingeniería](#prácticas-de-ingeniería)
- [Testing](#testing)
- [Cómo ejecutar el proyecto](#cómo-ejecutar-el-proyecto)
- [Deployment](#deployment)
- [CI/CD](#cicd)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Decisiones técnicas](#decisiones-técnicas)
- [Roadmap](#roadmap)
- [Autor](#autor)
- [Licencia](#licencia)

---

## Descripción

**Enterprise Operations Platform** es un backend empresarial diseñado para centralizar la gestión de operaciones internas de una organización: usuarios, roles, proyectos, tareas y auditoría de acciones.

El proyecto implementa un modelo de **control de acceso basado en roles (RBAC)** con tres niveles de permisos (`ADMIN`, `MANAGER`, `USER`), **autenticación con JWT** (access + refresh tokens), **documentación interactiva con OpenAPI 3.1**, testing automatizado con **Testcontainers**, y un pipeline de **CI/CD con GitHub Actions** que deploya automáticamente a **GCP Cloud Run** en cada push a `main`.

### Alcance

**Incluye:**
- ✅ Gestión de usuarios con roles y permisos (RBAC)
- ✅ CRUD completo de proyectos con estados y ownership
- ✅ Gestión de tareas con prioridades, estados y asignaciones
- ✅ Autenticación JWT con access + refresh tokens
- ✅ Documentación automática con Swagger UI
- ✅ Testing unitario, de integración y E2E
- ✅ Docker multi-stage + CI/CD + Deploy a Cloud Run
- ✅ Base de datos gestionada en Neon

**No incluye (fuera del alcance actual):**
- Frontend web (el proyecto se enfoca exclusivamente en backend)
- Notificaciones por email
- Integración con servicios de terceros (Slack, Jira, etc.)
- Reportes avanzados o analytics

---

## Estado del proyecto

| Componente | Estado |
|---|---|
| **Backend Spring Boot 4** | ✅ Producción en Cloud Run |
| **PostgreSQL 16 en Neon** | ✅ Producción |
| **23 endpoints REST** | ✅ Documentados en Swagger |
| **Autenticación JWT** | ✅ Access + refresh tokens |
| **RBAC con 3 roles** | ✅ Implementado |
| **Testing** | ✅ 15 tests pasando en CI |
| **CI/CD con GitHub Actions** | ✅ Auto-deploy en push a main |
| **Dockerfile multi-stage** | ✅ Optimizado |
| **Swagger UI público** | ✅ Accesible |

### Métricas

| Métrica | Valor |
|---|---|
| **Endpoints REST** | 23 |
| **Tests automatizados** | 15 |
| **Tablas PostgreSQL** | 5 (users, projects, tasks, audit_logs, flyway_schema_history) |
| **Archivos Java** | 47 |
| **Commits** | 9 |
| **Tiempo de arranque** | ~5 segundos |
| **Costo mensual** | $0 (free tier) |

---

## Stack técnico

### Backend
- **Java 21** (LTS)
- **Spring Boot 4.0.8**
- **Spring Framework 7.0.9**
- **Spring Security 7** (JWT)
- **Spring Data JPA**
- **Hibernate ORM 7.2.24**
- **Bean Validation** (Jakarta)
- **Flyway** (migraciones)
- **Lombok** (boilerplate reduction)

### Base de datos
- **PostgreSQL 16.15**
- **HikariCP** (connection pool)
- **Neon** (PostgreSQL gestionado en producción)

### Testing
- **JUnit 5**
- **Mockito** (unit tests)
- **Testcontainers** (integration tests con PostgreSQL real)
- **Spring Boot Test**
- **MockMvc** (E2E tests)
- **AssertJ** (assertions fluidas)
- **JaCoCo** (coverage)

### Documentación
- **springdoc-openapi 2.8.4**
- **OpenAPI 3.1**
- **Swagger UI**

### Build & DevOps
- **Maven 3.9.16** (wrapper incluido)
- **Docker** (multi-stage build)
- **Docker Compose** (dev local)
- **GitHub Actions** (CI/CD)
- **GCP Artifact Registry**

### Deploy
- **GCP Cloud Run** (backend serverless)
- **Neon Postgres** (base de datos)
- **Firebase Hosting** (landing del proyecto)

---

## Arquitectura

El proyecto sigue una **arquitectura por capas** con separación estricta de responsabilidades:

```
┌─────────────────────────────────────────────┐
│  Controllers (REST API)                     │
│  • Reciben requests HTTP                    │
│  • Validan input con @Valid                 │
│  • Delegan a services                       │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  Services (Lógica de negocio)               │
│  • Reglas de dominio                        │
│  • Transacciones (@Transactional)           │
│  • Validaciones de negocio                  │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  Repositories (Acceso a datos)              │
│  • Spring Data JPA                          │
│  • Queries derivadas + JPQL custom          │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  PostgreSQL                                 │
└─────────────────────────────────────────────┘
```

### Capas transversales

- **Security** — Filtros JWT, configuración de Spring Security, `@PreAuthorize`
- **Exceptions** — Manejo global de errores con `@RestControllerAdvice`
- **DTOs** — Separación entre entidades JPA y contratos de API
- **Config** — Beans de Spring (Security, PasswordEncoder, CORS)
- **Audit** — Preparado para registro automático de acciones (fuera del MVP)

---

## Modelo de datos

### Entidades principales

**User**
| Campo | Tipo | Notas |
|---|---|---|
| id | UUID | Primary key |
| email | String | Unique, not null |
| password | String | BCrypt hashed (strength 12) |
| fullName | String | Not null |
| role | Enum | ADMIN, MANAGER, USER |
| active | Boolean | Default true |
| createdAt | Timestamp | Auto (@CreatedDate) |
| updatedAt | Timestamp | Auto (@LastModifiedDate) |

**Project**
| Campo | Tipo | Notas |
|---|---|---|
| id | UUID | Primary key |
| name | String | Not null |
| description | Text | |
| status | Enum | ACTIVE, PAUSED, COMPLETED, ARCHIVED |
| owner | User | FK (@ManyToOne) |
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
| project | Project | FK (@ManyToOne) |
| assignee | User | FK nullable (@ManyToOne) |
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

```
User ────< Project (owner)
User ────< Task (assignee)
Project ──< Task
User ────< AuditLog
```

---

## Seguridad

### Autenticación

- **JWT** (JSON Web Tokens) con firma **HS512**
- **Access token** con expiración de **15 minutos**
- **Refresh token** con expiración de **7 días**
- Contraseñas hasheadas con **BCrypt** (strength 12)

### Estructura del JWT

```json
{
  "type": "access",
  "authorities": ["ROLE_USER"],
  "sub": "user@example.com",
  "iss": "enterprise-ops-platform",
  "iat": 1789774329,
  "exp": 1789775229
}
```

### Autorización (RBAC)

| Recurso | USER | MANAGER | ADMIN |
|---|---|---|---|
| Ver sus propias tareas | ✅ | ✅ | ✅ |
| Ver todas las tareas | ❌ | ✅ | ✅ |
| Crear tareas | ❌ | ✅ | ✅ |
| Asignar tareas a otros | ❌ | ✅ | ✅ |
| Crear proyectos | ❌ | ✅ | ✅ |
| Editar cualquier proyecto | ❌ | ✅ | ✅ |
| Eliminar/archivar proyectos | ❌ | ❌ | ✅ |
| Gestionar usuarios | ❌ | ❌ | ✅ |
| Ver audit log | ❌ | ❌ | ✅ |

**Implementación:**
- `@PreAuthorize` en métodos del controller
- Reglas de negocio en `UserService` y `ProjectService`
- `SecurityContext` con `UserDetails` personalizado

### Buenas prácticas aplicadas

- ✅ Nunca exponer contraseñas en respuestas (`@ToString(exclude = "password")`)
- ✅ Validación de JWT en cada request protegido (filtro `JwtAuthenticationFilter`)
- ✅ CORS configurado explícitamente con orígenes conocidos
- ✅ Headers de seguridad via Spring Security
- ✅ Manejo centralizado de excepciones
- ✅ Base64 obligatorio para `JWT_SECRET` (HS512)

---

## Endpoints de la API

**Base URL:** `https://enterprise-ops-api-931400252050.us-central1.run.app/api/v1`

### Autenticación

| Método | Endpoint | Descripción | Auth |
|---|---|---|---|
| POST | `/auth/register` | Registrar nuevo usuario | No |
| POST | `/auth/login` | Iniciar sesión | No |

### Usuarios

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| GET | `/users/me` | Ver perfil propio | Autenticado |
| GET | `/users` | Listar usuarios (paginado) | ADMIN |
| GET | `/users/{id}` | Ver usuario por ID | ADMIN |
| PUT | `/users/{id}` | Actualizar usuario | ADMIN o propio |
| DELETE | `/users/{id}` | Desactivar usuario (soft delete) | ADMIN |

### Proyectos

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| GET | `/projects` | Listar proyectos (paginado, filtrado por rol) | Autenticado |
| GET | `/projects/{id}` | Ver proyecto | Acceso al proyecto |
| POST | `/projects` | Crear proyecto | MANAGER+ |
| PUT | `/projects/{id}` | Actualizar proyecto | MANAGER+ o owner |
| DELETE | `/projects/{id}` | Archivar proyecto | ADMIN |

### Tareas

| Método | Endpoint | Descripción | Rol |
|---|---|---|---|
| GET | `/tasks/me` | Mis tareas asignadas | Autenticado |
| GET | `/projects/{id}/tasks` | Tareas de un proyecto | Acceso al proyecto |
| GET | `/tasks/{id}` | Ver tarea | Assignee o MANAGER+ |
| POST | `/projects/{id}/tasks` | Crear tarea | MANAGER+ o owner |
| PUT | `/tasks/{id}` | Actualizar tarea | Assignee o MANAGER+ |
| PATCH | `/tasks/{id}/status` | Cambiar status | Assignee o MANAGER+ |
| DELETE | `/tasks/{id}` | Eliminar tarea | ADMIN |

### Convenciones

- **Versionado:** `/api/v1/...`
- **Paginación:** `?page=0&size=20&sort=createdAt,desc`
- **Errores:** formato estándar con `timestamp`, `status`, `error`, `message`, `path`

**Formato de error de ejemplo:**

```json
{
  "timestamp": "2026-09-19T00:33:19.882Z",
  "status": 403,
  "error": "Forbidden",
  "message": "Solo un ADMIN puede cambiar el rol de un usuario",
  "path": "/api/v1/users/d46321ea-..."
}
```

---

## Prácticas de ingeniería

### Diseño de software

- **Arquitectura por capas** con separación estricta de responsabilidades
- **DTOs** para desacoplar entidades JPA de contratos de API
- **Records de Java 21** para DTOs inmutables
- **Inyección de dependencias** por constructor (con Lombok `@RequiredArgsConstructor`)
- **Programación defensiva** con validaciones en múltiples niveles

### APIs REST

- Convenciones REST: sustantivos en plural, verbos HTTP correctos
- Códigos de estado HTTP semánticos (200, 201, 204, 400, 401, 403, 404, 409)
- Versionado desde el día 1 (`/api/v1/`)
- Paginación y filtros en endpoints de listado
- Documentación con OpenAPI 3.1 + Swagger UI
- `@ControllerAdvice` para manejo centralizado de excepciones

### Seguridad

- JWT con access + refresh tokens
- BCrypt con strength 12
- RBAC con `@PreAuthorize` + reglas de negocio
- Bean Validation en DTOs de entrada
- CORS explícito
- Secrets por variables de entorno (nunca en código)

### Persistencia

- Migraciones versionadas con **Flyway**
- **`ddl-auto: validate`** (Hibernate no toca el schema, Flyway lo hace)
- Índices en columnas de búsqueda frecuente (email, role, status, foreign keys)
- Constraints a nivel de BD
- Transacciones declarativas con `@Transactional`
- Soft delete (campo `active` en lugar de DELETE físico)

### Calidad de código

- Tests con cobertura de servicios, repositorios y controllers
- Commits siguiendo **Conventional Commits** (`feat:`, `fix:`, `chore:`, `docs:`, `test:`, `ci:`)
- Código formateado consistentemente
- Sin warnings de compilación críticos

### DevOps

- **Docker multi-stage** (build + runtime separados)
- Usuario no-root en la imagen final
- **Docker Compose** para desarrollo local
- **CI/CD con GitHub Actions**
- **Deploy automatizado** a Cloud Run en push a `main`
- **Imágenes versionadas** por commit SHA + tag `latest`

---

## Testing

### Estrategia

| Tipo | Herramienta | Qué prueba |
|---|---|---|
| **Unit tests** | JUnit 5 + Mockito | Lógica de servicios aislada |
| **Integration tests** | Spring Boot Test + Testcontainers | Repositorios con PostgreSQL real |
| **E2E tests** | MockMvc | Endpoints HTTP completos |

### Ubicación

```
src/test/java/com/torotech/enterpriseops/
├── unit/
│   └── service/
│       └── AuthServiceTest.java          # 5 tests con Mockito
├── integration/
│   ├── repository/
│   │   └── UserRepositoryIT.java         # 5 tests con Testcontainers
│   └── controller/
│       └── AuthControllerIT.java         # 5 tests E2E con MockMvc
└── config/
    └── TestcontainersConfig.java         # Configuración de PostgreSQL
```

### Ejecutar tests

```bash
# Todos los tests
./mvnw test

# Solo unit tests
./mvnw test -Dtest="*Test"

# Solo integration tests
./mvnw test -Dtest="*IT"

# Con reporte de cobertura
./mvnw verify
# Abre target/site/jacoco/index.html
```

### Resultado actual

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Testcontainers** levanta un contenedor PostgreSQL real en cada test de integración, garantizando que se pruebe contra la misma BD que en producción (no H2, no mocks).

---

## Cómo ejecutar el proyecto

### Requisitos previos

- **Java 21** o superior ([Temurin](https://adoptium.net/))
- **Maven 3.9+** (o usar el wrapper incluido `./mvnw`)
- **Docker Desktop** ([descargar](https://www.docker.com/products/docker-desktop/))

### Ejecución local con Docker Compose

```bash
# 1. Clonar el repo
git clone https://github.com/fabianmm83/enterprise-ops-platform.git
cd enterprise-ops-platform

# 2. Levantar PostgreSQL con Docker
docker compose up -d postgres

# 3. Configurar el JWT_SECRET (variable de entorno)
# En Windows CMD:
set JWT_SECRET=ZGV2LXNlY3JldC1rZXktZm9yLWVudGVycHJpc2Utb3BzLXBsYXRmb3JtLWNoYW5nZS10aGlzLWluLXByb2R1Y3Rpb24tMTIzNDU2Nzg5MA==

# En Linux/Mac:
export JWT_SECRET=ZGV2LXNlY3JldC1rZXktZm9yLWVudGVycHJpc2Utb3BzLXBsYXRmb3JtLWNoYW5nZS10aGlzLWluLXByb2R1Y3Rpb24tMTIzNDU2Nzg5MA==

# 4. Ejecutar la aplicación
./mvnw spring-boot:run
```

**La API estará disponible en:**
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health check: http://localhost:8080/actuator/health

### Variables de entorno

Crea un archivo `.env` en la raíz (basado en `.env.example`):

```env
# Database
DB_HOST=localhost
DB_PORT=5433
DB_NAME=enterprise_ops
DB_USER=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=ZGV2LXNlY3JldC1rZXktZm9yLWVudGVycHJpc2Utb3BzLXBsYXRmb3JtLWNoYW5nZS10aGlzLWluLXByb2R1Y3Rpb24tMTIzNDU2Nzg5MA==
JWT_ACCESS_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

---

## Deployment

### Arquitectura de producción

```
┌──────────────────────────────────────────────┐
│  Cliente (Swagger UI / HTTP)                 │
└────────────────┬─────────────────────────────┘
                 │ HTTPS
                 ▼
┌──────────────────────────────────────────────┐
│  GCP Cloud Run                               │
│  https://enterprise-ops-api-931400252050...  │
│  (Spring Boot 4 en Docker)                   │
└────────────────┬─────────────────────────────┘
                 │ JDBC (SSL)
                 ▼
┌──────────────────────────────────────────────┐
│  Neon PostgreSQL 16                          │
│  (Base de datos gestionada)                  │
└──────────────────────────────────────────────┘
```

**Características del deploy:**
- **Serverless:** escala a cero cuando no hay tráfico
- **HTTPS automático** con certificado gestionado por Google
- **Región:** `us-central1`
- **Memoria:** 512 MiB
- **CPU:** 1 vCPU
- **Max instancias:** 3 (para evitar costos inesperados)
- **Timeout:** 60 segundos

### Deploy manual

```bash
# 1. Build de la imagen
docker build -t us-central1-docker.pkg.dev/sistema-bunker/enterprise-ops/api:latest .

# 2. Push a Artifact Registry
docker push us-central1-docker.pkg.dev/sistema-bunker/enterprise-ops/api:latest

# 3. Deploy a Cloud Run
gcloud run deploy enterprise-ops-api \
  --image=us-central1-docker.pkg.dev/sistema-bunker/enterprise-ops/api:latest \
  --region=us-central1 \
  --platform=managed \
  --allow-unauthenticated \
  --port=8080 \
  --memory=512Mi \
  --cpu=1 \
  --min-instances=0 \
  --max-instances=3 \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod" \
  --set-env-vars="DATABASE_URL=jdbc:postgresql://..." \
  --set-env-vars="DATABASE_USERNAME=..." \
  --set-env-vars="DATABASE_PASSWORD=..." \
  --set-env-vars="JWT_SECRET=..."
```

---

## CI/CD

El proyecto usa **GitHub Actions** para automatizar:

1. **Testing** en cada push a `main`
2. **Build de la imagen Docker**
3. **Push a Artifact Registry**
4. **Deploy a Cloud Run** (solo si los tests pasan)

**Workflow:** [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml)

### Jobs

**Job 1: `test`**
- Corre en cada push a `main`
- Levanta JDK 21
- Ejecuta `./mvnw clean test` (15 tests con Testcontainers)
- Sube reportes de tests y cobertura como artifacts

**Job 2: `deploy`**
- Solo corre si `test` pasa
- Se autentica con GCP usando Service Account
- Construye la imagen Docker con 2 tags (`latest` + commit SHA)
- Sube los 2 tags a Artifact Registry
- Deploya a Cloud Run
- Imprime la URL del servicio

### Secrets requeridos

| Secret | Descripción |
|---|---|
| `GCP_SA_KEY` | Service Account key (JSON) para autenticar con GCP |
| `GCP_PROJECT_ID` | `sistema-bunker` |
| `DATABASE_URL` | JDBC URL de Neon |
| `DATABASE_USERNAME` | Usuario de Neon |
| `DATABASE_PASSWORD` | Password de Neon |
| `JWT_SECRET` | Secret Base64 para firmar JWTs |

### Badge

[![Deploy to Cloud Run](https://github.com/fabianmm83/enterprise-ops-platform/actions/workflows/deploy.yml/badge.svg)](https://github.com/fabianmm83/enterprise-ops-platform/actions/workflows/deploy.yml)

---

## Estructura del proyecto

```
enterprise-ops-platform/
├── .github/
│   └── workflows/
│       └── deploy.yml                 # CI/CD pipeline
├── .mvn/wrapper/                      # Maven Wrapper
├── docs/
│   ├── architecture.md
│   └── decisions/                     # ADRs
├── src/
│   ├── main/
│   │   ├── java/com/torotech/enterpriseops/
│   │   │   ├── EnterpriseOpsApplication.java
│   │   │   ├── config/
│   │   │   │   ├── PasswordEncoderConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── ProjectController.java
│   │   │   │   └── TaskController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── entity/
│   │   │   │   ├── User.java
│   │   │   │   ├── Role.java
│   │   │   │   ├── Project.java
│   │   │   │   ├── ProjectStatus.java
│   │   │   │   ├── Task.java
│   │   │   │   ├── TaskStatus.java
│   │   │   │   └── TaskPriority.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   ├── UserNotFoundException.java
│   │   │   │   ├── ProjectNotFoundException.java
│   │   │   │   ├── TaskNotFoundException.java
│   │   │   │   └── EmailAlreadyExistsException.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ProjectRepository.java
│   │   │   │   └── TaskRepository.java
│   │   │   ├── security/
│   │   │   │   ├── JwtService.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   └── service/
│   │   │       ├── AuthService.java
│   │   │       ├── UserService.java
│   │   │       ├── ProjectService.java
│   │   │       └── TaskService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/
│   │           └── V1__init_schema.sql
│   └── test/
│       └── java/com/torotech/enterpriseops/
│           ├── config/
│           │   └── TestcontainersConfig.java
│           ├── unit/service/
│           │   └── AuthServiceTest.java
│           └── integration/
│               ├── controller/AuthControllerIT.java
│               └── repository/UserRepositoryIT.java
├── .dockerignore
├── .env.example
├── .gitignore
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── LICENSE
```

---

## Decisiones técnicas

### ¿Por qué Java 21?
Última versión LTS con soporte hasta 2031+. Trae **virtual threads**, **records**, **pattern matching**, y **sealed classes**, que mejoran la expresividad y el performance.

### ¿Por qué Spring Boot 4?
Framework estándar de facto para backend Java. Spring Boot 4 incluye **Spring Framework 7**, **Jakarta EE 11**, y soporte nativo para Java 21. Trae mejoras en arranque, observabilidad y configuración.

### ¿Por qué PostgreSQL?
Base de datos relacional robusta, con soporte para **JSONB**, **UUID nativo**, **full-text search**, e **índices parciales**. Estándar de facto para aplicaciones empresariales.

### ¿Por qué JWT?
Autenticación **stateless** ideal para APIs REST. Permite **escalar horizontalmente** sin compartir sesiones. Combinación de **access + refresh tokens** balancea seguridad y UX.

### ¿Por qué Flyway?
Migraciones **versionadas en el código**, control de cambios de schema, reproducible en cualquier entorno. A diferencia de `ddl-auto: update`, es determinista y auditable.

### ¿Por qué Testcontainers?
Tests de integración con **PostgreSQL real**, no H2 ni mocks. Detectan problemas que los tests unitarios no ven (queries JPQL, constraints, transacciones).

### ¿Por qué GCP Cloud Run?
**Serverless con containers**, escala a cero, tier gratis generoso (2M requests/mes). Ideal para portafolios y aplicaciones de bajo tráfico.

### ¿Por qué Neon?
**PostgreSQL gestionado** gratis (0.5 GB), sin administración, con backups automáticos y SSL obligatorio. Compatible 100% con PostgreSQL 16.

---

## Roadmap

### ✅ Completado

- [x] Setup con Java 21 + Spring Boot 4 + Maven Wrapper
- [x] PostgreSQL 16 en Docker + Flyway
- [x] Entidades JPA con relaciones (`@ManyToOne`)
- [x] Autenticación JWT con access + refresh tokens
- [x] RBAC con 3 roles (ADMIN, MANAGER, USER)
- [x] CRUD completo de Users, Projects y Tasks
- [x] Paginación y filtros
- [x] Manejo global de excepciones
- [x] Documentación con Swagger/OpenAPI 3.1
- [x] Tests unitarios, de integración y E2E
- [x] Dockerfile multi-stage
- [x] CI/CD con GitHub Actions
- [x] Deploy a GCP Cloud Run
- [x] Base de datos en Neon
- [x] Landing del proyecto en Firebase Hosting

### 🚧 En progreso / Planeado

- [ ] Aumentar cobertura de tests a 70% (services restantes)
- [ ] Endpoint `/auth/refresh` para renovar tokens
- [ ] Sistema de auditoría (`audit_logs` poblado)
- [ ] Rate limiting con Bucket4j
- [ ] Custom domain (`api.torotech.dev`)
- [ ] Frontend React (opcional, ya cubierto por Swagger)

---

## Autor

**Fabian Moreno Monroy**
Full Stack Engineer · Estudiante de Ingeniería Mecatrónica (UNAM)

- 🌐 [Portafolio](https://fabianmmcv.web.app)
- 💼 [LinkedIn](https://www.linkedin.com/in/fabián-moreno-monroy83)
- 💻 [GitHub](https://github.com/fabianmm83)
- 📧 [Email](mailto:fabianmm83@hotmail.com)

---

## Licencia

Este proyecto está bajo la [Licencia MIT](LICENSE).

---

## Agradecimientos

Proyecto desarrollado como pieza central de portafolio profesional, con foco en demostrar prácticas de ingeniería de software aplicadas en entornos empresariales reales.