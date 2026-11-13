# Arquitectura Ecogarzones

## Microservicio (Spring Boot)
```mermaid
flowchart LR
    A[App Android<br/>Jetpack Compose + MVVM] -- Retrofit / JSON --> C
    subgraph Backend Spring Boot
        C[Controllers REST<br/>/api/usuarios /solicitudes /incidencias /inventario] --> S[Services<br/>reglas + validación]
        S --> R[Repositories Spring Data JPA]
    end
    R --> D[(PostgreSQL<br/>Render / Supabase / AWS RDS)]
```

## Proyecto Android (MVVM)
```mermaid
flowchart TB
    UI[ui/screens - Composables] --> VM[ViewModels]
    VM --> REPO[SolicitudRepository]
    REPO --> ROOM[(Room / SQLite<br/>autenticación + caché offline)]
    REPO --> API[Retrofit ApiService]
    API --> BE[Backend Spring Boot]
    UI --> NAT[Recursos nativos: Cámara, Galería, Notificaciones]
```

## Endpoints
| Método | Ruta | Descripción |
|---|---|---|
| POST | /api/usuarios/registro | Registra usuario (rol según dominio del correo) |
| POST | /api/usuarios/login | Autentica |
| PUT | /api/usuarios/perfil?email= | Actualiza perfil |
| GET/POST | /api/solicitudes | Lista / crea |
| GET/PUT | /api/solicitudes/{id} | Detalle / actualiza (estado, personal, checklist, feedback) |
| GET/POST | /api/incidencias[?solicitudId=] | Lista / crea |
| PUT | /api/incidencias/{id} | Cambia estado |
| GET/POST | /api/inventario | Lista / crea |
| PUT | /api/inventario/{id} | Edita ítem / stock |
