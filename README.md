# 🗺️ Rutea API

API REST para gestión de rutas turísticas, puntos de interés, reseñas y usuarios.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Tecnologías](#tecnologías)
- [Requisitos](#requisitos)
- [Instalación](#instalación)
- [Uso](#uso)
- [API Endpoints](#api-endpoints)
- [Tests](#tests)
- [Contribuir](#contribuir)

## ✨ Características

- ✅ CRUD completo para 5 entidades
- ✅ Filtrado avanzado en endpoints GET
- ✅ Validación de datos con Bean Validation
- ✅ Manejo centralizado de excepciones
- ✅ DTOs para transferencia de datos
- ✅ Operaciones PATCH para actualización parcial
- ✅ Sistema de logs con Logback
- ✅ Tests unitarios (Service + Controller)
- ✅ Documentación OpenAPI 3.0
- ✅ Colección Postman incluida

## 🛠️ Tecnologías

- **Java** 21
- **Spring Boot** 3.2
- **Spring Data JPA**
- **MariaDB** 10.11
- **Lombok**
- **Bean Validation**
- **JUnit 5** + Mockito
- **Wiremock**

## 📦 Requisitos Previos

- Java JDK 21 o superior
- Maven 3.8+
- MariaDB 10.11+
- Postman (opcional, para probar API)

## 🚀 Instalación

### 1. Clonar repositorio

### 2. Configurar Base de Datos

Iniciar MariaDB/MySQL y crear base de datos:

### 3. Configurar application.properties

Editar `src/main/resources/application.properties`:

### 4. Compilar y ejecutar

La API estará disponible en: `http://localhost:8080`

## 📚 API Endpoints

### Categorías

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/categorias` | Listar todas las categorías |
| GET | `/categorias/{id}` | Obtener categoría por ID |
| POST | `/categorias` | Crear nueva categoría |
| PUT | `/categorias/{id}` | Actualizar categoría |
| PATCH | `/categorias/{id}` | Actualización parcial |
| DELETE | `/categorias/{id}` | Eliminar categoría |

**Filtros disponibles:** `activa`, `nombre`, `ordenPrioridad`

### Puntos de Interés

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/puntos` | Listar todos los puntos |
| GET | `/puntos/{id}` | Obtener punto por ID |
| POST | `/puntos` | Crear nuevo punto |
| PUT | `/puntos/{id}` | Actualizar punto |
| PATCH | `/puntos/{id}` | Actualización parcial |
| DELETE | `/puntos/{id}` | Eliminar punto |

**Filtros disponibles:** `categoriaId`, `abiertoActualmente`, `nombre`, `puntuacionMedia`

*(Añadir el resto de entidades...)*

## 🧪 Ejecutar Tests

\`\`\`bash
# Todos los tests
mvn test

# Solo tests de Service
mvn test -Dtest=*ServiceTest

# Solo tests de Controller
mvn test -Dtest=*ControllerTest
\`\`\`

## 📬 Importar Colección Postman

1. Abrir Postman
2. Click en "Import"
3. Seleccionar archivo `Rutea_API.postman_collection.json`
4. Configurar variable de entorno `base_url = http://localhost:8080`

## 📖 Documentación OpenAPI

La especificación completa está en `rutea.yaml`.

Visualizar en Swagger Editor: https://editor.swagger.io/

## 📊 Diagrama de Base de Datos

(Añadir diagrama ER aquí)

## 🤝 Contribuir

1. Fork el proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Añadir nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abrir Pull Request

## 📄 Licencia

Este proyecto es parte de la asignatura **Acceso a Datos** (Centro San Valero).
