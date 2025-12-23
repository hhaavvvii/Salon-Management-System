PRAGMA foreign_keys = ON;

-- Employees
CREATE TABLE employees (
                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                           first_name TEXT NOT NULL,
                           last_name TEXT NOT NULL,
                           position TEXT NOT NULL,
                           active INTEGER NOT NULL DEFAULT 1
);

-- Users
CREATE TABLE users (
                       id INTEGER PRIMARY KEY AUTOINCREMENT,
                       username TEXT NOT NULL UNIQUE,
                       password_hash TEXT NOT NULL,
                       role TEXT NOT NULL CHECK (role IN ('ADMIN', 'MASTER')),
                       employee_id INTEGER UNIQUE,

                       FOREIGN KEY (employee_id)
                           REFERENCES employees(id)
                           ON DELETE SET NULL
);

-- Clients
CREATE TABLE clients (
                         id INTEGER PRIMARY KEY AUTOINCREMENT,

                         first_name TEXT NOT NULL,
                         phone TEXT NOT NULL,

                         last_name TEXT,
                         email TEXT,
                         notes TEXT
);

-- Services
CREATE TABLE services (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          name TEXT NOT NULL,
                          category TEXT,
                          price REAL NOT NULL,
                          duration_minutes INTEGER NOT NULL,
                          active INTEGER NOT NULL DEFAULT 1
);

-- Appointments
CREATE TABLE appointments (
                              id INTEGER PRIMARY KEY AUTOINCREMENT,
                              client_id INTEGER NOT NULL,
                              employee_id INTEGER NOT NULL,
                              service_id INTEGER NOT NULL,
                              start_time TEXT NOT NULL,
                              end_time TEXT NOT NULL,
                              status TEXT NOT NULL CHECK (status IN ('PLANNED', 'DONE', 'CANCELED')),

                              FOREIGN KEY (client_id)
                                  REFERENCES clients(id)
                                  ON DELETE CASCADE,

                              FOREIGN KEY (employee_id)
                                  REFERENCES employees(id)
                                  ON DELETE CASCADE,

                              FOREIGN KEY (service_id)
                                  REFERENCES services(id)
                                  ON DELETE RESTRICT
);

-- Индекс для проверки конфликтов по времени
CREATE INDEX idx_appointments_employee_time
    ON appointments (employee_id, start_time, end_time);

-- Payments
CREATE TABLE payments (
                          id INTEGER PRIMARY KEY AUTOINCREMENT,
                          appointment_id INTEGER NOT NULL UNIQUE,
                          amount REAL NOT NULL,
                          created_at TEXT NOT NULL,
                          method TEXT NOT NULL CHECK (method IN ('CASH', 'CARD', 'OTHER')),

                          FOREIGN KEY (appointment_id)
                              REFERENCES appointments(id)
                              ON DELETE CASCADE
);

-- Inventory (optional)
CREATE TABLE inventory (
                           id INTEGER PRIMARY KEY AUTOINCREMENT,
                           name TEXT NOT NULL,
                           quantity INTEGER NOT NULL,
                           threshold INTEGER NOT NULL
);
