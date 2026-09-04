
-- Login: admin@pesaloom.local / PesaLoomAdmin!2026
INSERT INTO users (id, email, password_hash, first_name, last_name, role, enabled, created_at)
VALUES (
    gen_random_uuid(),
    'admin@pesaloom.local',
    '$2y$12$jI/rWlItWIksAoT5Dmwl6OAM6UnFcr7MXlvs1wbHm1CixA7LjHX8S',
    'PesaLoom',
    'Admin',
    'ADMIN',
    TRUE,
    now()
);
