-- Fix the admin password hash to properly match "admin123"
UPDATE users 
SET password = '$2a$10$A063nmSqDZypWNeKuKSDVeNagcKYdhHrn2z3AG8Etw5D6XIhxWh.S' 
WHERE email = 'admin@dashboard.com';
