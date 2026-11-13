# Ecogarzones Backend (Spring Boot + PostgreSQL)

## Ejecutar en local
```bash
docker compose up -d db          # PostgreSQL en :5432
mvn spring-boot:run              # API en http://localhost:8080
```
Al primer arranque se cargan usuarios e inventario de prueba (ver `DataSeeder`).
Prueba rápida:
```bash
curl -X POST localhost:8080/api/usuarios/login -H "Content-Type: application/json" \
     -d '{"email":"matias@adminecogarzones.com","password":"matias123"}'
```

## Pruebas unitarias (JUnit 5 + Mockito + H2)
```bash
mvn test
```

## Deploy en la nube
**Render**: Web Service desde este repo con *Docker*. Variables de entorno:
`DB_URL` (formato JDBC, ej. `jdbc:postgresql://HOST:5432/DB?sslmode=require`), `DB_USER`, `DB_PASSWORD`.
Base de datos: Render PostgreSQL o Supabase (usa el host/puerto del *connection string*).
Render inyecta `PORT` automáticamente.

Luego en Android cambia `BASE_URL` en `RetrofitClient.kt` por `https://TU-SERVICIO.onrender.com/`.
