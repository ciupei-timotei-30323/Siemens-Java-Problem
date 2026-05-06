# 🚂 Train Ticketing Application

A Spring Boot REST API for managing train bookings, routes, and schedules. Supports customer ticket booking with email confirmation, route search with changeover detection, and admin management operations.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Reference](#api-reference)
    - [Public Endpoints](#public-endpoints)
    - [Admin Endpoints](#admin-endpoints)
- [Features](#features)
- [Examples](#examples)
- [Running Tests](#running-tests)

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Language |
| Spring Boot 3.4.x | Framework |
| Spring Data JPA / Hibernate | ORM |
| H2 (in-memory) | Database |
| Spring Security | Authentication & Authorization |
| Spring Mail | Email notifications |
| Mailtrap | Email sandbox (dev/testing) |
| Lombok | Boilerplate reduction |
| JUnit 5 + MockMvc | Testing |
| Maven | Build tool |

---

## Project Structure

```
src/
├── main/
│   ├── java/com/siemens/ticketapp/
│   │   ├── TicketAppApplication.java
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   ├── controller/
│   │   │   ├── BookingController.java
│   │   │   └── AdminController.java
│   │   ├── dto/
│   │   │   ├── BookingRequest.java
│   │   │   ├── TripSearchRequest.java
│   │   │   └── TripOption.java
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── model/
│   │   │   ├── Station.java
│   │   │   ├── Route.java
│   │   │   ├── RouteStation.java
│   │   │   ├── Train.java
│   │   │   ├── Schedule.java
│   │   │   └── Booking.java
│   │   ├── repository/
│   │   │   ├── StationRepository.java
│   │   │   ├── RouteRepository.java
│   │   │   ├── RouteStationRepository.java
│   │   │   ├── TrainRepository.java
│   │   │   ├── ScheduleRepository.java
│   │   │   └── BookingRepository.java
│   │   └── service/
│   │       ├── EmailService.java
│   │       ├── BookingService.java
│   │       ├── RouteFinderService.java
│   │       └── AdminService.java
│   └── resources/
│       ├── application.properties
│       └── data.sql
└── test/
    └── java/com/siemens/ticketapp/
        ├── BaseIntegrationTest.java
        ├── BookingControllerTest.java
        ├── RouteSearchControllerTest.java
        └── AdminControllerTest.java
```

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- A free [Mailtrap](https://mailtrap.io) account (for email testing)

### Running the Application

```bash
# Clone the repository
git clone https://github.com/your-username/train-ticketing-app.git
cd train-ticketing-app

# Run the application
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

### H2 Database Console

Navigate to `http://localhost:8080/h2-console` with these settings:

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:ticketdb` |
| Username | `sa` |
| Password | *(leave empty)* |

The database is pre-populated with sample data on startup via `data.sql`.

---

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Mail — replace with your Mailtrap credentials
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=YOUR_MAILTRAP_USERNAME
spring.mail.password=YOUR_MAILTRAP_PASSWORD

# Admin credentials
spring.security.user.name=admin
spring.security.user.password=admin123
```

---

## Preloaded Data

The following data is seeded automatically on startup:

**Stations:** Budapest, Vienna, Bratislava, Prague, Warsaw

**Routes:**
- `Budapest-Vienna Express`: Budapest → Bratislava → Vienna
- `Vienna-Prague Express`: Vienna → Bratislava → Prague

**Trains:**
- `IC-101` (capacity: 100) on the Budapest-Vienna route
- `IC-202` (capacity: 80) on the Vienna-Prague route

**Schedules:**
- IC-101: 08:00 and 14:00 on 2026-06-01
- IC-202: 09:00 and 16:00 on 2026-06-01

---

## API Reference

### Authentication

- **Public endpoints** (`/api/public/**`) — no authentication required
- **Admin endpoints** (`/api/admin/**`) — HTTP Basic Auth required

```
Username: admin
Password: admin123
```

---

### Public Endpoints

#### Book a Ticket

```
POST /api/public/book
```

**Request body:**
```json
{
  "scheduleId": 1,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "seatsBooked": 2
}
```

**Success response (200 OK):**
```json
{
  "id": 1,
  "schedule": {
    "id": 1,
    "departureTime": "2026-06-01T08:00:00",
    "delayMinutes": null,
    "train": {
      "id": 1,
      "name": "IC-101",
      "capacity": 100
    }
  },
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "seatsBooked": 2
}
```

**Overbooking error (409 Conflict):**
```json
{
  "error": "Not enough seats available. Requested: 999, Available: 100"
}
```

**Validation error (400 Bad Request):**
```json
{
  "customerEmail": "Invalid email address",
  "seatsBooked": "Must book at least 1 seat"
}
```

> A confirmation email is sent to the provided address after every successful booking.

---

#### Search for Trips

```
POST /api/public/search
```

**Request body:**
```json
{
  "origin": "Budapest",
  "destination": "Vienna"
}
```

**Direct connection response (200 OK):**
```json
[
  {
    "stations": ["Budapest", "Bratislava", "Vienna"],
    "trainNames": ["IC-101"],
    "departureTimes": ["2026-06-01T08:00:00", "2026-06-01T14:00:00"],
    "requiresChangeover": false
  }
]
```

**Changeover connection (Budapest → Prague):**
```json
[
  {
    "stations": ["Budapest", "Bratislava", "Vienna", "Prague"],
    "trainNames": ["IC-101", "IC-202"],
    "departureTimes": ["2026-06-01T08:00:00", "2026-06-01T09:00:00"],
    "requiresChangeover": true
  }
]
```

**No connection found (400 Bad Request):**
```json
{
  "error": "No connection found between Budapest and Warsaw"
}
```

**Unknown station (400 Bad Request):**
```json
{
  "error": "Destination station not found: Tokyo"
}
```

---

### Admin Endpoints

All admin endpoints require Basic Auth (`admin` / `admin123`).

#### Train Management

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/trains` | List all trains |
| POST | `/api/admin/trains` | Add a new train |
| PUT | `/api/admin/trains/{id}` | Update a train |
| DELETE | `/api/admin/trains/{id}` | Delete a train |

**Add a train — request:**
```json
{
  "name": "IC-303",
  "capacity": 120,
  "route": { "id": 1 }
}
```

**Add a train — response (200 OK):**
```json
{
  "id": 3,
  "name": "IC-303",
  "capacity": 120,
  "route": { "id": 1, "name": "Budapest-Vienna Express" }
}
```

---

#### Route Management

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/routes` | List all routes |
| POST | `/api/admin/routes` | Add a new route |
| PUT | `/api/admin/routes/{id}` | Update a route |
| DELETE | `/api/admin/routes/{id}` | Delete a route |

---

#### View Bookings for a Train

```
GET /api/admin/trains/{trainId}/bookings
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "customerName": "John Doe",
    "customerEmail": "john@example.com",
    "seatsBooked": 2,
    "schedule": {
      "id": 1,
      "departureTime": "2026-06-01T08:00:00"
    }
  }
]
```

---

#### Report a Train Delay

```
POST /api/admin/schedules/{scheduleId}/delay?minutes=30
```

**Response (200 OK):**
```
Delay reported and customers notified.
```

> All customers with bookings on this schedule are automatically sent a delay notification email.

---

## Features

### Overbooking Prevention

Booking uses a `@Transactional` method with a live seat count query, preventing race conditions when multiple users book simultaneously. If requested seats exceed availability, a `409 Conflict` is returned.

### Route Finding

The route search supports:
- **Direct connections** — both stations on the same route in the correct order
- **One-changeover connections** — a shared intermediate station between two routes

If no connection exists, a clear error message is returned.

### Email Notifications

Two types of emails are sent automatically:

- **Booking confirmation** — sent to the customer after a successful booking
- **Delay notification** — sent to all affected customers when an admin reports a delay

Emails are handled by `EmailService` and sent via SMTP. Failures are logged but do not break the booking flow.

---

## Running Tests

```bash
mvn test
```

The test suite uses `MockMvc` integration tests covering:

- Successful ticket booking
- Overbooking prevention
- Validation errors (invalid email, zero seats)
- Direct route search
- Changeover route search
- Unknown and unconnected stations
- Admin authentication (authorized and unauthorized)
- Delay reporting

Expected output:
```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
```