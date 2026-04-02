-- Fix the admin password hash to properly match "admin123"
UPDATE users 
SET password = '$2a$10$7LiqHCfXLT45g2Z8iPZI..z6RujBMOvihmMdrIyyBJtx23l5viybm' 
WHERE email = 'admin@dashboard.com';
