PRAGMA foreign_keys = ON;

-- ===== EMPLOYEES =====
INSERT INTO employees (first_name, last_name, position, active)
VALUES
    ('Admin', 'User', 'Administrator', 1),
    ('Anna', 'Master', 'Hair Stylist', 1);

-- ===== USERS =====
-- ADMIN user (no employee_id)
INSERT INTO users (username, password_hash, role, employee_id)
VALUES
    ('admin', 'admin', 'ADMIN', NULL);

-- MASTER user (linked to employee)
INSERT INTO users (username, password_hash, role, employee_id)
VALUES
    ('master', 'master', 'MASTER', 2);
