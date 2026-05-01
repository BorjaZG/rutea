# Rutea API

API REST para gestión de rutas turísticas, puntos de interés, reseñas y usuarios.

## Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Requisitos](#requisitos)
- [Instalación y arranque](#instalación-y-arranque)
- [Variables de entorno](#variables-de-entorno)
- [API Endpoints](#api-endpoints)
- [Tests](#tests)
- [Postman](#postman)
- [Documentación OpenAPI](#documentación-openapi)

## Características

- CRUD completo para 5 entidades: Categorías, Puntos de Interés, Reseñas, Rutas y Usuarios
- Filtrado por múltiples campos en todos los endpoints GET
- Validación de datos con Jakarta Bean Validation
- Manejo centralizado de excepciones (`GlobalExceptionHandler`) con handlers locales por controller
- DTOs independientes para entrada (`*InDto`) y salida (`*OutDto`)
- Operaciones PATCH para actualización parcial de campos
- Versiones v1 y v2 de la API de Rutas (soft delete, campos extendidos en v2)
- Documentación Swagger/OpenAPI 3 en `/swagger-ui.html`
- Sistema de logs con Logback (fichero rotativo diario en `logs/rutea.log`)
- 10 tests unitarios: 5 de servicio (Mockito) + 5 de controller (`@WebMvcTest`)

## Tecnologías

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.3.3 |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MariaDB 11.3 (prod) / H2 in-memory (dev) |
| Validación | Jakarta Validation |
| Mapeo DTO | ModelMapper 3.2.0 |
| Documentación | springdoc-openapi 2.6.0 |
| Lombok | Reducción de boilerplate |
| Tests | JUnit 5 + Mockito + WireMock 3.9.1 |
| Contenedores | Docker + Docker Compose |

## Requisitos

- Java JDK 17 o superior
- Maven 3.8+ (o usar el wrapper `./mvnw`)
- Docker y Docker Compose (para arranque con MariaDB o producción)

## Instalación y arranque

### Clonar el repositorio

```bash
git clone <url-del-repo>
cd rutea
```

El proyecto incluye un `Makefile` con atajos para todos los comandos habituales.

### Opción A — Desarrollo local con H2 (sin Docker)

No requiere base de datos externa. H2 se crea en memoria al arrancar.

```bash
make dev
```

- API: `http://localhost:8080`
- Consola H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:ruteadb`
  - Usuario: `sa` / Contraseña: *(vacío)*

### Opción B — Desarrollo con MariaDB en Docker

Arranca solo la base de datos en Docker y la app en local con el perfil `prod`.

```bash
# 1. Copiar y rellenar las credenciales
cp .env.sample .env

# 2. Arrancar solo MariaDB y luego la app
make db-up
make prod
```

Para parar la base de datos:

```bash
make db-down
```

> Descomenta el bloque `volumes` en `docker-compose.dev.yaml` para persistir datos entre reinicios.

### Opción C — Producción completa con Docker

Construye el JAR y la imagen, luego levanta API + MariaDB juntos.

```bash
# 1. Copiar y rellenar las credenciales
cp .env.sample .env

# 2. Construir el JAR y la imagen Docker
make build
docker build -t rutea-api .

# 3. Levantar todos los servicios
make up
```

Comandos útiles:

```bash
docker compose logs -f rutea-api   # Seguir logs de la API
make down                           # Parar y eliminar contenedores
```

- API: `http://localhost:8080`

### Referencia rápida del Makefile

| Comando | Descripción |
|---------|-------------|
| `make dev` | Arrancar con H2 (perfil dev) |
| `make prod` | Arrancar con MariaDB (perfil prod) |
| `make build` | Compilar JAR sin tests |
| `make test` | Ejecutar todos los tests |
| `make db-up` | Levantar solo MariaDB en Docker |
| `make db-down` | Parar MariaDB |
| `make up` | Docker Compose producción completa |
| `make down` | Parar todos los contenedores |

## Variables de entorno

Copia `.env.sample` a `.env` y rellena los valores antes de arrancar con MariaDB:

| Variable | Descripción |
|----------|-------------|
| `MARIADB_USER` | Usuario de la base de datos |
| `MARIADB_DATABASE` | Nombre de la base de datos |
| `MARIADB_ROOT_PASSWORD` | Contraseña del root de MariaDB |
| `RUTEA_DATABASE_PASSWORD` | Contraseña del usuario de la app |
| `PROFILE` | Perfil de Spring activo en producción (`prod`) |

## API Endpoints

Swagger UI disponible en `http://localhost:8080/swagger-ui.html` cuando la app esté corriendo.

### Categorías

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/categorias` | Listar categorías |
| GET | `/categorias/{id}` | Obtener categoría por ID |
| POST | `/categorias` | Crear categoría |
| PUT | `/categorias/{id}` | Actualizar categoría |
| PATCH | `/categorias/{id}` | Actualización parcial |
| DELETE | `/categorias/{id}` | Eliminar categoría |

Filtros en GET: `activa`, `nombre`, `ordenPrioridad`

### Puntos de Interés

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/puntos` | Listar puntos |
| GET | `/puntos/{id}` | Obtener punto por ID |
| POST | `/puntos` | Crear punto |
| PUT | `/puntos/{id}` | Actualizar punto |
| PATCH | `/puntos/{id}` | Actualización parcial |
| DELETE | `/puntos/{id}` | Eliminar punto |

Filtros en GET: `categoriaId`, `abiertoActualmente`, `nombre`

### Reseñas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/resenas` | Listar reseñas |
| GET | `/resenas/{id}` | Obtener reseña por ID |
| POST | `/resenas` | Crear reseña |
| PUT | `/resenas/{id}` | Actualizar reseña |
| PATCH | `/resenas/{id}` | Actualización parcial |
| DELETE | `/resenas/{id}` | Eliminar reseña |

Filtros en GET: `puntoId`, `usuarioId`, `valoracion`

### Rutas — v1

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/v1/rutas` | Listar rutas |
| GET | `/v1/rutas/{id}` | Obtener ruta por ID |
| POST | `/v1/rutas` | Crear ruta |
| PUT | `/v1/rutas/{id}` | Actualizar ruta |
| PATCH | `/v1/rutas/{id}` | Actualización parcial |
| DELETE | `/v1/rutas/{id}` | Eliminar ruta (hard delete) |

Filtros en GET: `usuarioId`, `publica`, `dificultad`

### Rutas — v2

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/v2/rutas` | Listar rutas (respuesta extendida) |
| GET | `/v2/rutas/{id}` | Obtener ruta por ID (respuesta extendida) |
| POST | `/v2/rutas` | Crear ruta con etiquetas |
| PUT | `/v2/rutas/{id}` | Actualizar ruta |
| DELETE | `/v2/rutas/{id}` | Eliminar ruta (soft delete) |

Diferencias respecto a v1: incluye campos `etiquetas` y `totalPuntos` en la respuesta. El DELETE marca la ruta como eliminada sin borrarla de la base de datos.

### Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/usuarios` | Listar usuarios |
| GET | `/usuarios/{id}` | Obtener usuario por ID |
| POST | `/usuarios` | Crear usuario |
| PUT | `/usuarios/{id}` | Actualizar usuario |
| PATCH | `/usuarios/{id}` | Actualización parcial |
| DELETE | `/usuarios/{id}` | Eliminar usuario |

Filtros en GET: `premium`, `username`, `nivelExperiencia`

## Tests

```bash
# Todos los tests
./mvnw test

# Una clase concreta
./mvnw test -Dtest=RutaServiceTest

# Todos los tests de servicio
./mvnw test -Dtest="*ServiceTest"

# Todos los tests de controller
./mvnw test -Dtest="*ControllerTest"
```

Los tests de controller usan `@WebMvcTest` + `MockMvc` con `@MockBean` en la capa de servicio.
Los tests de servicio usan Mockito con repositorios mockeados.

## Postman

El repositorio incluye tres ficheros para importar en Postman:

| Fichero | Descripción |
|---------|-------------|
| `Rutea.postman_collection.json` | Colección completa de peticiones |
| `local.postman_environment.json` | Entorno local (`base_url = http://localhost:8080`) |
| `apiman.postman_environment.json` | Entorno Apiman |

Para importar: Postman → Import → seleccionar los ficheros.

## Documentación OpenAPI

La especificación completa está en `rutea.yaml` en la raíz del proyecto.

Visualizar online en [editor.swagger.io](https://editor.swagger.io/) o directamente en Swagger UI al arrancar la app: `http://localhost:8080/swagger-ui.html`
