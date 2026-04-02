-- Password is "admin123" encrypted with BCrypt (Cost=10)
-- $2a$10$U67A60X19f/s2kCReOOSM.xW9nDE17Qf59yG97gM8o.8IclL3vMCq
INSERT INTO users (id, name, email, password, role, status)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'System Admin',
    'admin@dashboard.com',
    '$2a$10$U67A60X19f/s2kCReOOSM.xW9nDE17Qf59yG97gM8o.8IclL3vMCq',
    'ADMIN',
    'ACTIVE'
);
