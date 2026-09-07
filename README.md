# Basketball Stats API

Backend del Basketball Shot Tracker. Registra tiros, clasifica zonas de cancha y calcula efectividad por zona.

## Stack

- Java 21
- Spring Boot 4.1.1
- PostgreSQL 17
- Flyway
- Docker Compose
- GitHub Actions

## Ejecutar localmente

Requiere Docker Desktop con integración WSL habilitada:

```bash
docker compose up --build -d
```

Verificar disponibilidad:

```text
GET http://localhost:8080/actuator/health
```

Para detener los servicios:

```bash
docker compose down
```

## OpenAPI / Swagger UI

Con la API levantada, la documentación interactiva está disponible en:

```text
http://localhost:8080/swagger-ui.html
```

La especificación OpenAPI en JSON está disponible en:

```text
http://localhost:8080/v3/api-docs
```

Usa `Authentication` para obtener un JWT y pégalo en el botón `Authorize` de Swagger UI como Bearer token. La documentación es pública solo para facilitar el desarrollo local; antes de producción deberá protegerse o deshabilitarse.

## Flujo de API

Todas las requests, excepto login y healthcheck, requieren `Authorization: Bearer <accessToken>`.

1. Autenticarse:

```text
POST /api/auth/login
```

```json
{
  "username": "bastian",
  "password": "changeme123"
}
```

La credencial anterior es solo para desarrollo local. No debe reutilizarse en producción.

2. Crear un jugador:

```text
POST /api/players
```

```json
{
  "name": "Bastian",
  "dominantHand": "RIGHT",
  "heightCm": 169
}
```

3. Registrar un tiro:

```text
POST /api/shots
```

```json
{
  "playerId": "<playerId>",
  "posX": 0,
  "posY": 7,
  "made": true
}
```

Las coordenadas están en metros: el origen es el centro del aro, `posX` negativo representa la izquierda y `posY` apunta hacia media cancha.

4. Consultar historial:

```text
GET /api/players/<playerId>/shots
```

5. Consultar estadísticas por zona:

```text
GET /api/players/<playerId>/stats/zones
```

6. Consultar estadísticas globales:

```text
GET /api/players/<playerId>/stats
```

La respuesta incluye intentos, aciertos, fallos, efectividad global y puntos anotados.

## Tests

Los tests de integración levantan PostgreSQL 17 automáticamente con Testcontainers:

```bash
./mvnw test
```

## CI

GitHub Actions ejecuta tests contra PostgreSQL real mediante Testcontainers y construye la imagen Docker en cada push o pull request a `main`.

El despliegue en GCP y Google OIDC quedan fuera de `v0.1.0`.
