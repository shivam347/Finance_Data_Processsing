-- Insert a test VIEWER user
-- Password for this user is: admin123
INSERT INTO users (id, name, email, password, role, status)
VALUES ('test_viewer_1', 'John Viewer', 'viewer1@gmail.com', '$2a$10$xCXljUPfZwePnL3asD/3YenvKCtnizzCmIllnaJokAvzwSXSrHcBW', 'VIEWER', 'ACTIVE');

-- Insert some test financial records for John Viewer
INSERT INTO financial_records (id, amount, type, category, record_date, notes, created_by)
VALUES 
('test_record_1', 50000.00, 'INCOME', 'Salary', '2026-04-01', 'April Salary', 'test_viewer_1'),
('test_record_2', 1500.50, 'EXPENSE', 'Groceries', '2026-04-02', 'Weekly groceries', 'test_viewer_1'),
('test_record_3', 2500.00, 'EXPENSE', 'Utilities', '2026-04-03', 'Electricity Bill', 'test_viewer_1'),
('test_record_4', 800.00, 'EXPENSE', 'Entertainment', '2026-04-04', 'Movie night', 'test_viewer_1'),
('test_record_5', 400.00, 'EXPENSE', 'Food', '2026-04-04', 'Lunch', 'test_viewer_1');
