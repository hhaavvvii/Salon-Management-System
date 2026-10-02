# Salon Management System

A desktop application for managing the daily operations of a beauty salon.

This project was developed as a university team project using Java, JavaFX, SQLite, and JDBC. The application provides functionality for managing clients, employees, services, appointments, payments, and reports.

<img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 42 14" src="https://github.com/user-attachments/assets/d1f3884d-e5a3-42d2-a6ac-7602440e7c69" /><img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 57 57" src="https://github.com/user-attachments/assets/eb019756-8942-4b6c-a460-f2ba4bb2a13f" />
<img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 57 50" src="https://github.com/user-attachments/assets/2758c5d1-7121-499d-836d-9fe42c392ee5" />
<img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 57 39" src="https://github.com/user-attachments/assets/69945507-0d8b-4796-bbef-b2351c5eb669" />
<img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 50 43" src="https://github.com/user-attachments/assets/6f80b3ab-893f-41f5-8ab2-1d26a735498f" />
<img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 49 47" src="https://github.com/user-attachments/assets/fb24bb08-a9f0-42d2-b679-1e3633d56a45" />
<img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 49 41" src="https://github.com/user-attachments/assets/01efa7a0-2397-43b6-a125-9006fa835371" />
<img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 48 47" src="https://github.com/user-attachments/assets/91c019dd-187f-47be-bd7f-91314b744e13" />
<img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 48 36" src="https://github.com/user-attachments/assets/a483676c-952b-40a7-9c51-92f7442d97dc" />
<img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 48 27" src="https://github.com/user-attachments/assets/17f893d4-c44b-4c86-8034-971611465994" />
<img width="1280" height="832" alt="Снимок экрана — 2026-10-02 в 14 48 20" src="https://github.com/user-attachments/assets/7bd72428-09a4-4e6b-893e-3e7ef86f0eaf" />

## Features

- User authentication
- Role-based access
- Client management
- Employee management
- Service management
- Appointment scheduling
- Appointment status management
- Payment management
- Appointment conflict validation
- Revenue and operational reports
- CSV report export
- SQLite database integration

## Technologies

- Java 21
- JavaFX 21
- SQLite
- JDBC
- Maven
- FXML
- CSS
- JUnit 5

## Architecture

The application uses a layered architecture:

```text
JavaFX UI
    ↓
Controllers
    ↓
Services
    ↓
DAO
    ↓
SQLite Database
```
### Project Layers

- `controllers` — handles user interface interactions
- `service` — contains business logic
- `dao` — provides database access
- `model` — contains application entities and enums
- `dto` — data transfer objects used for reports
- `util` — utility classes, database connection and CSV export
- `exceptions` — custom application exceptions
- `app` — application entry point

## Main Modules

### Authentication

The system provides user authentication and role-based access.

### Clients

The application allows users to:

- Add clients
- Edit client information
- Search and filter clients
- Deactivate clients

### Employees

The application allows users to:

- Add employees
- Edit employee information
- Search and filter employees
- Deactivate employees

### Services

The system manages salon services, including:

- Service name
- Category
- Price
- Duration
- Active status

### Appointments

The appointment module allows users to:

- Create appointments
- Select a client, employee, and service
- Set appointment date and time
- Change appointment status
- Validate appointment time conflicts

Available appointment statuses:

- `PLANNED`
- `COMPLETED`
- `CANCELLED`

### Payments

The system provides functionality for managing payments associated with appointments.

### Reports

The application provides reports related to:

- Revenue
- Revenue by employee
- Revenue by service
- Employee workload
- Client activity
- Service statistics

Reports can also be exported to CSV format.

## Database

The application uses SQLite as a local relational database.

The database schema is located in:

`src/main/resources/sql/schema.sql`

Sample database data is located in:

`src/main/resources/sql/seed.sql`

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── ...
│   │       ├── app/
│   │       ├── controllers/
│   │       ├── dao/
│   │       ├── dto/
│   │       ├── exceptions/
│   │       ├── model/
│   │       ├── service/
│   │       └── util/
│   │
│   └── resources/
│       ├── fxml/
│       ├── icons/
│       ├── sql/
│       └── styles/
│
└── test/
```
## How to Run
Requirements:
- Java 21
- Maven
  
Run with Maven

On macOS/Linux:
`./mvnw clean javafx:run`

On Windows:
`mvnw.cmd clean javafx:run`

# Academic Project

This project was developed as part of a university team project.
The system was designed and implemented collaboratively, with individual development contributions within the team.

Author:
Alua Slambek
