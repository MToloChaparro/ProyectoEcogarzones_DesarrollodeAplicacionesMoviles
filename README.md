# GestionEcogarzones - Proyecto Mínimo Viable (MVP)

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-green?style=for-the-badge&logo=android)
![Room Database](https://img.shields.io/badge/Room-SQLite-blue?style=for-the-badge&logo=sqlite)

## Desarrollador
*   **Matias Toloza Chaparro**
*   **Asignatura:** Desarrollo de Aplicaciones Móviles - Sección 010D
*   **Carrera:** Ingeniería en Informática - Duoc UC sede Maipú
*   **Profesora:** Viviana Poblete

## Contexto del Negocio
Ecogarzones es una Pyme con más de seis años de trayectoria continua en el rubro de banquetería y producción de eventos sociales y corporativos en la Región Metropolitana (cumpleaños, matrimonios, cenas corporativas y activaciones de marca). La empresa ofrece una amplia oferta integral que abarca la provisión de garzones, bartenders, cocineros, coperos, animadores y supervisores de logística. Su propuesta de valor se basa en la cordialidad, la excelencia operativa y la adaptabilidad a las necesidades específicas del cliente.

## Problema y Necesidades
El modelo operativo actual presenta severos cuellos de botella e ineficiencias críticas:
*   **Tiempos de gestión elevados:** Cotizaciones por múltiples rondas de chat o llamadas, causando demoras y pérdida de clientes.
*   **Discrepancias y errores:** Omisiones respecto a restricciones alimentarias, número de invitados o insumos.
*   **Baja trazabilidad:** Falta de un panel centralizado para auditar eventos en simultáneo, horarios y control de insumos.
*   **Escalamiento complejo:** El cliente no identifica a quién dirigirse ante una incidencia en terreno.

## Objetivo del Proyecto
Digitalizar y automatizar el catálogo de servicios, permitiendo cotizaciones instantáneas y transparentes. Además, dotar al equipo en terreno y a la administración de una plataforma nativa Android que otorgue visibilidad en tiempo real del flujo del servicio, control de inventario y reporte ágil de incidencias con evidencia fotográfica.


## Funcionalidades Implementadas

### Módulo de Cliente
*   **Cotización Inteligente:** Formulario detallado de eventos (fecha con calendario nativo, dirección, tipo de evento).
*   **Cálculo Automático de Personal:** Sugerencia en tiempo real de garzones, cocineros, bartenders y coperos basada en el número de invitados (reglas de negocio de la industria).
*   **Selección de Menús y Barras:** Catálogo interactivo con descripción de platos/bebidas, control de cantidades exactas y validación de stock vs invitados.
*   **Logística de Insumos:** Selección de provisión de insumos (quién lleva qué).
*   **Resumen y Confirmación:** Pantalla de revisión de datos antes de enviar, con pantalla de éxito animada y mensaje de contacto (48 hrs).
*   **Reporte de Incidencias:** Canal directo para que el cliente reporte problemas adjuntando fotos desde cámara o galería.

### Módulo de Administración
*   **Dashboard Centralizado:** Métricas en tiempo real (Total Eventos, Pendientes, Total Invitados).
*   **Gestión de Eventos:** Visualización de solicitudes y cambio de estados (Pendiente, Confirmado, En Proceso, Finalizado, Cancelado).
*   **Gestión de Incidencias:** Listado de reportes (clientes/personal), cambio de estado (Reportada, En Proceso, Resuelta) y visualización de fotos de evidencia en pantalla completa.
*   **Control de Inventario:**
    *   Registro de insumos con categoría, unidad de medida, costo unitario y proveedor.
    *   Visualización del **Valor Total del Inventario** en tiempo real con formato de moneda.
    *   Edición de stock, estados físicos (Disponible, En uso, Dañado, Vencido, Defectuoso, En mantenimiento) y registro de fecha de adquisición.
    *   Doble validación (diálogo de confirmación) antes de aplicar cambios críticos en la base de datos.

## Tecnologías y Arquitectura
*   **Lenguaje:** Kotlin.
*   **Interfaz Gráfica:** Jetpack Compose (Material Design 3).
*   **Arquitectura:** MVVM (Model-View-ViewModel) desacoplada.
*   **Base de Datos Local:** Room (SQLite) con soporte para Flows y corrutinas.
*   **Carga de Imágenes:** Coil.
*   **Recursos Nativos Integrados:** Cámara (FileProvider), Galería y Calendario nativo (DatePicker).

### Estructura del Proyecto
```text
com.example.gestionecogarzones/
├── data/
│   ├── local/          # Entidades, DAOs y Base de Datos Room
│   └── repository/     # Repositorio central
├── ui/
│   ├── components/     # Componentes reutilizables (inputs, mapas)
│   ├── navigation/     # Rutas de navegación
│   ├── screens/        # Pantallas (admin, cliente, login)
│   └── theme/          # Colores corporativos y tipografía
├── viewmodel/          # Lógica de negocio (MVVM)
└── MainActivity.kt     # Entry point y NavHost
```

# (ENG) GestionEcogarzones - Minimum Viable Product (MVP)

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple?style=for-the-badge&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-green?style=for-the-badge&logo=android)
![Room Database](https://img.shields.io/badge/Room-SQLite-blue?style=for-the-badge&logo=sqlite)

A native Android application developed as a Minimum Viable Product (MVP) for **Ecogarzones**, an SME specializing in catering and event management.

## Authors
*   **Matias Toloza Chaparro**
*   **Subject:** Movile App Development - Section 010D
*   **Career:** Informatic Engineer - Duoc UC sede Maipú
*   **Teacher:** Viviana Poblete

## Business Context
Ecogarzones is an SME with over six years of continuous experience in the catering and event production industry in the Metropolitan Region of Chile (birthdays, weddings, corporate dinners, and brand activations). The company offers a comprehensive service including waiters, bartenders, chefs, dishwashers, entertainers, and logistics supervisors. Its value proposition is based on cordiality, operational excellence, and adaptability to specific client needs.

## Problem Statement
The current operational model presents critical bottlenecks and inefficiencies:
*   **High management times:** Quotations require multiple rounds of chat or calls, causing delays and losing potential clients.
*   **Discrepancies and errors:** Omissions regarding dietary restrictions, number of guests, or supplies.
*   **Low traceability:** Lack of a centralized dashboard to audit simultaneous events, schedules, and supply control.
*   **Complex escalation:** Clients do not know who to contact in case of an on-site incident.

## Project Goal
To digitalize and automate the service catalog, allowing instant and transparent quotations. Additionally, to provide on-site staff and administration with a native Android platform that offers real-time visibility of the service flow, inventory control, and agile incident reporting with photographic evidence.


## Implemented Features

### Client Module
*   **Smart Quotation:** Detailed event form (date picker, location, event type).
*   **Automatic Staff Calculation:** Real-time suggestion of waiters, chefs, bartenders, and dishwashers based on guest count, following industry business rules.
*   **Menu and Bar Selection:** Interactive catalog with dish/drink descriptions, exact quantity control, and stock vs. guests validation.
*   **Supply Logistics:** Selection of supply provision responsibilities.
*   **Summary and Confirmation:** Data review screen before submission, featuring an animated success screen and a 48-hour contact message.
*   **Incident Reporting:** Direct channel for clients to report issues by attaching photos from camera or gallery.

### Admin Module
*   **Centralized Dashboard:** Real-time metrics (Total Events, Pending Events, Total Guests).
*   **Event Management:** View requests and update status (Pending, Confirmed, In Progress, Finished, Canceled).
*   **Incident Management:** List of reports from clients and staff, status updates (Reported, In Progress, Resolved), and full-screen evidence photo viewing.
*   **Inventory Control:**
    *   Supply registration with category, unit of measure, unit cost, and supplier.
    *   Real-time Total Inventory Value visualization with currency formatting.
    *   Stock editing, physical states (Available, In Use, Damaged, Expired, Defective, Under Maintenance), and acquisition date tracking.
    *   Double validation (confirmation dialog) before applying critical changes to the database.

## Technologies and Architecture
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose (Material Design 3)
*   **Architecture:** Decoupled MVVM (Model-View-ViewModel)
*   **Local Database:** Room (SQLite) with Flows and Coroutines support
*   **Image Loading:** Coil
*   **Native Resources:** Camera (FileProvider), Gallery and Native DatePicker

### Project Structure
```text
com.example.gestionecogarzones/
├── data/
│   ├── local/          # Entities, DAOs, and Room Database
│   └── repository/     # Central repository
├── ui/
│   ├── components/     # Reusable components (inputs, maps)
│   ├── navigation/     # Navigation routes
│   ├── screens/        # Screens (admin, client, login)
│   └── theme/          # Corporate colors and typography
├── viewmodel/          # Business logic (MVVM)
└── MainActivity.kt     # Entry point and NavHost
```

