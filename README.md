

# Train Ticketing Application

A Spring Boot REST API for managing train bookings, routes, and schedules. Supports customer ticket booking with email confirmation, route search with changeover detection, and admin management operations.

---

## Solution Overview

This application was designed to meet the following requirements:
1. **Ticket Booking:** Users can book multiple tickets on a schedule. Overbooking is prevented via strict live capacity checks (`@Transactional` query), and an automated confirmation email is dispatched to the customer upon success.
2. **Route Finding:** Users can search for possible connections between any two stations. The application detects **direct routes** as well as routes requiring a **single changeover**. It responds with an appropriate error if no viable path is available.
3. **Admin Controls:** - Manage the entire topology by creating, modifying, and deleting **Stations**, **Routes**, and **Trains**.
   - Review live **Bookings** made on any given train.
   - Report **Delays** on specific schedules. When an administrator flags a delay, every customer who booked a ticket on that schedule automatically receives an email notification regarding the delay.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Reference & Examples](#api-reference--examples)
    - [Public Endpoints](#public-endpoints)
    - [Admin Endpoints](#admin-endpoints)
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

```text
src/
├── main/
│   ├── java/SiemensTicketApp/App/
│   │   ├── Config/          # Security configs
│   │   ├── Controller/      # REST API Endpoints
│   │   ├── DTO/             # Request/Response payloads
│   │   ├── Exception/       # Global error handling
│   │   ├── Model/           # JPA Entities (Train, Route, Schedule, Booking, Station)
│   │   ├── Repository/      # Spring Data JPA Repositories
│   │   └── Service/         # Core business logic (Booking, Email, Routing, Admin)
│   └── resources/
│       ├── application.properties # App configs
│       └── data.sql         # Seed data
└── test/                    # Integration Tests

```

---

## Getting Started

### Prerequisites

* Java 21+
* Maven 3.8+
* A free [Mailtrap](https://mailtrap.io) account (for email testing)

### Running the Application

```bash
# Run the application using the Maven wrapper
./mvnw spring-boot:run

```

The application starts on `http://localhost:8080`.

### H2 Database Console

Navigate to `http://localhost:8080/h2-console` with these settings:

| Field | Value |
| --- | --- |
| JDBC URL | `jdbc:h2:mem:ticketdb` |
| Username | `sa` |
| Password | *(leave empty)* |

The database is pre-populated with sample data on startup via `data.sql`.

---

## Configuration

Edit `src/main/resources/application.properties` to ensure your email provider is set up:

```properties
# Mail — replace with your Mailtrap credentials or other SMTP details
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

* `Budapest-Vienna Express`: Budapest → Bratislava → Vienna
* `Vienna-Prague Express`: Vienna → Bratislava → Prague

**Trains & Schedules:**

* `IC-101` (capacity: 100) on Budapest-Vienna (Schedules: 08:00 and 14:00 on 2026-06-01)
* `IC-202` (capacity: 80) on Vienna-Prague (Schedules: 09:00 and 16:00 on 2026-06-01)

---

## API Reference & Examples

### Authentication

* **Public endpoints** (`/api/public/`) — no authentication required
* **Admin endpoints** (`/api/admin/`) — HTTP Basic Auth required (`Username: admin`, `Password: admin123`)

---

### Public Endpoints

#### 1. Book a Ticket

`POST /api/public/book`

**Request:**

```json
{
  "scheduleId": 1,
  "customerName": "John Doe",
  "customerEmail": "john@example.com",
  "seatsBooked": 2
}

```

**Success Response (200 OK):**

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
  "customerEmail": "john@example.com",
  "customerName": "John Doe",
  "seatsBooked": 2
}

```

*(An email confirmation is asynchronously sent to `john@example.com`)*

**Overbooking Error Response (409 Conflict):**

```json
{
  "error": "Not enough seats available. Requested: 5, Available: 2"
}

```

#### 2. Search for Trips

`POST /api/public/search`

**Request (Direct Connection Search):**

```json
{
  "origin": "Budapest",
  "destination": "Vienna"
}

```

**Response (200 OK):**

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

**Request (Changeover Search - Budapest to Prague):**

```json
{
  "origin": "Budapest",
  "destination": "Prague"
}

```

**Response (200 OK):**

```json
[
  {
    "stations": ["Budapest", "Bratislava", "Vienna", "Prague"],
    "trainNames": ["IC-101", "IC-202"],
    "departureTimes": [
      "Train 1: 2026-06-01T08:00 | Train 2: 2026-06-01T16:00"
    ],
    "requiresChangeover": true
  }
]

```

**No Route Found Response (400 Bad Request):**

```json
{
  "error": "No connection found between Budapest and Warsaw"
}

```

---

### Admin Endpoints

All admin operations require Basic Auth (`admin` / `admin123`).

#### 1. Station Management

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/admin/station` | Add a new station |
| PUT | `/api/admin/station/{id}` | Modify an existing station |
| DELETE | `/api/admin/station/{id}` | Delete a station |

**Create a Station Request:**
`POST /api/admin/station`

```json
{
  "name": "Berlin"
}

```

**Create a Station Response (200 OK):**

```json
{
  "id": 6,
  "name": "Berlin"
}

```

**Modify a Station Request:**
`PUT /api/admin/station/6`

```json
{
  "name": "Berlin Hbf"
}

```

**Modify a Station Response (200 OK):**

```json
{
  "id": 6,
  "name": "Berlin Hbf"
}

```

**Delete a Station:**
`DELETE /api/admin/station/6`
Returns: `204 No Content`

---

#### 2. Route Management

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/admin/routes` | View all routes |
| POST | `/api/admin/routes` | Add a new route |
| PUT | `/api/admin/routes/{id}` | Modify an existing route |
| DELETE | `/api/admin/routes/{id}` | Delete a route |

**Add Route Request:**
`POST /api/admin/routes`

```json
{
  "name": "Berlin Express",
  "routeStationsId": [4, 6]
}

```

**Add Route Response (200 OK):**

```json
{
  "id": 3,
  "name": "Berlin Express",
  "routeStations": [
    {
      "id": 7,
      "station": { "id": 4, "name": "Prague" },
      "stopOrder": 0
    },
    {
      "id": 8,
      "station": { "id": 6, "name": "Berlin Hbf" },
      "stopOrder": 1
    }
  ]
}

```

**Modify Route Request (Updates order/stations):**
`PUT /api/admin/routes/3`

```json
{
  "name": "Berlin Fast Express",
  "routeStationsId": [2, 4, 6]
}

```

**Delete a Route:**
`DELETE /api/admin/routes/3`
Returns: `204 No Content`

---

#### 3. Train Management

| Method | Endpoint | Description |
| --- | --- | --- |
| GET | `/api/admin/trains` | View all trains |
| POST | `/api/admin/trains` | Add a new train |
| PUT | `/api/admin/trains/{id}` | Modify an existing train |
| DELETE | `/api/admin/trains/{id}` | Delete a train |

**Add Train Request:**
`POST /api/admin/trains`

```json
{
  "name": "IC-303",
  "capacity": 120,
  "routeId": 1
}

```

**Add Train Response (200 OK):**

```json
{
  "id": 3,
  "name": "IC-303",
  "capacity": 120,
  "route": {
    "id": 1,
    "name": "Budapest-Vienna Express",
    "routeStations": [...]
  }
}

```

**Modify Train Request (Update name/capacity/route):**
`PUT /api/admin/trains/3`

```json
{
  "name": "IC-303-Updated",
  "capacity": 150,
  "routeId": 2
}

```

**Delete a Train:**
`DELETE /api/admin/trains/3`
Returns: `204 No Content`

---

#### 4. View Bookings for a Train

`GET /api/admin/trains/{trainId}/bookings`

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "customerEmail": "john@example.com",
    "customerName": "John Doe",
    "seatsBooked": 2,
    "schedule": {
      "id": 1,
      "departureTime": "2026-06-01T08:00:00"
    }
  }
]

```

---

#### 5. Report a Train Delay

Allows admins to register delays on a given schedule. *This automatically dispatches an email to all customers who booked a ticket for this specific schedule.*

`POST /api/admin/schedules/{scheduleId}/delay?minutes=30`

**Response (200 OK):**

```
Delay reported and customers notified.

```

---

## Running Tests

Run the integration tests using Maven:

```bash
./mvnw test

```

The test suite leverages `MockMvc` to guarantee application stability, ensuring:

* Successful ticket booking logic
* Prevention of concurrency-based overbooking
* Input validation (invalid emails, bad capacities)
* Direct routing search algorithms
* One-changeover routing search algorithms
* Security rules mapping out unauthenticated Admin requests
* Delay report mechanisms and notifications

**Expected output:**

```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
```

---
