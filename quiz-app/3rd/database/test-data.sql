-- Test data for Quiz Microservice
-- Run this after the application creates the tables

USE testmian_quiz_app_v2;

-- Insert roles
INSERT INTO roles (role_name, description, created_by) VALUES
('Super Admin', 'Full system access', 1),
('Admin', 'Administrative access', 1),
('Candidate', 'Quiz participant', 1);

-- Insert auth methods
INSERT INTO auth_methods (method_key, display_name) VALUES
('password', 'Password'),
('magic_link', 'Magic Link'),
('oauth', 'OAuth');

-- Insert invite statuses
INSERT INTO invite_statuses (status_key, display_name) VALUES
('Pending', 'Pending'),
('Activated', 'Activated'),
('Expired', 'Expired');

-- Insert user statuses
INSERT INTO user_statuses (status_key, display_name) VALUES
('Active', 'Active'),
('Inactive', 'Inactive'),
('Suspended', 'Suspended');

-- Insert magic link purposes
INSERT INTO magic_link_purposes (purpose_key, description) VALUES
('LOGIN', 'User login'),
('RESET_PASSWORD', 'Password reset');

-- Insert delivery statuses
INSERT INTO delivery_statuses (status_key, display_name) VALUES
('Pending', 'Pending'),
('Sent', 'Sent'),
('Failed', 'Failed');

-- Insert assignment statuses
INSERT INTO assignment_statuses (status_key, display_name) VALUES
('Assigned', 'Assigned'),
('In Progress', 'In Progress'),
('Completed', 'Completed'),
('Expired', 'Expired');

-- Insert notification types
INSERT INTO notification_types (type_key, display_name) VALUES
('Email', 'Email Notification'),
('System', 'System Notification');

-- Insert test users
INSERT INTO users (role_id, full_name, email, password_hash, auth_method, invite_status, status, created_by) VALUES
(1, 'Himanshu', 'superadmin@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'magic_link', 'Activated', 'Active', 1),
(2, 'Manoj', 'admin@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'magic_link', 'Activated', 'Active', 1),
(3, 'John Doe', 'candidate@test.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'magic_link', 'Activated', 'Active', 1);

-- Insert difficulty levels
INSERT INTO difficulty_levels (difficulty_key, display_name) VALUES
('easy', 'Easy'),
('medium', 'Medium'),
('hard', 'Hard');

-- Insert question types
INSERT INTO question_types (type_key, display_name) VALUES
('multiple_choice', 'Multiple Choice'),
('true_false', 'True/False'),
('short_answer', 'Short Answer');

-- Insert question categories
INSERT INTO question_categories (category_name, description, created_by) VALUES
('Geography', 'Geography related questions', 1),
('Mathematics', 'Math related questions', 1),
('Science', 'Science related questions', 1),
('Computer Science', 'CS related questions', 1),
('Algorithms', 'Algorithm related questions', 1);

-- Insert test questions
INSERT INTO questions (category_id, question_type_id, difficulty_id, question_text, created_by) VALUES
(1, 1, 1, 'What is the capital of France?', 1),
(2, 1, 1, 'What is 2 + 2?', 1),
(3, 1, 2, 'What is the largest planet in our solar system?', 1),
(4, 1, 2, 'What does CPU stand for?', 1),
(5, 1, 3, 'What is the time complexity of binary search?', 1);

-- Insert question options
INSERT INTO question_options (question_id, option_text, is_correct, created_by) VALUES
-- Question 1: Capital of France
(1, 'Paris', true, 1),
(1, 'London', false, 1),
(1, 'Berlin', false, 1),
(1, 'Madrid', false, 1),

-- Question 2: 2 + 2
(2, '3', false, 1),
(2, '4', true, 1),
(2, '5', false, 1),
(2, '6', false, 1),

-- Question 3: Largest planet
(3, 'Earth', false, 1),
(3, 'Jupiter', true, 1),
(3, 'Saturn', false, 1),
(3, 'Mars', false, 1),

-- Question 4: CPU
(4, 'Central Processing Unit', true, 1),
(4, 'Computer Processing Unit', false, 1),
(4, 'Central Program Unit', false, 1),
(4, 'Computer Program Unit', false, 1),

-- Question 5: Binary search complexity
(5, 'O(n)', false, 1),
(5, 'O(log n)', true, 1),
(5, 'O(n²)', false, 1),
(5, 'O(1)', false, 1);

-- Insert test quizzes
INSERT INTO quizzes (title, description, duration_minutes, difficulty_id, created_by) VALUES
('General Knowledge Quiz', 'Test your general knowledge with this comprehensive quiz', 30, 1, 1),
('Math Basics Quiz', 'Basic mathematics questions for beginners', 15, 1, 1),
('Computer Science Quiz', 'Advanced computer science concepts', 45, 2, 2);

-- Insert quiz questions
INSERT INTO quiz_questions (quiz_id, question_id, marks) VALUES
-- General Knowledge Quiz
(1, 1, 1),
(1, 2, 1),
(1, 3, 1),

-- Math Basics Quiz
(2, 2, 1),

-- Computer Science Quiz
(3, 4, 1),
(3, 5, 1);

-- Insert test quiz assignments (removed to avoid conflicts)
-- INSERT INTO quiz_assignments (quiz_id, candidate_id, unique_link, expires_at, assignment_status_id, assigned_by) VALUES
-- (1, 3, 'unique-link-123', DATE_ADD(NOW(), INTERVAL 7 DAY), 1, 1),
-- (2, 3, 'unique-link-456', DATE_ADD(NOW(), INTERVAL 3 DAY), 1, 1);

-- Insert test magic links
INSERT INTO magic_links (user_id, token_hash, purpose_id, expires_at, delivery_status_id) VALUES
(1, SHA2('test-token-123', 256), 1, DATE_ADD(NOW(), INTERVAL 24 HOUR), 2),
(2, SHA2('test-token-456', 256), 1, DATE_ADD(NOW(), INTERVAL 24 HOUR), 2),
(3, SHA2('test-token-789', 256), 1, DATE_ADD(NOW(), INTERVAL 24 HOUR), 2);

-- Insert test audit logs (optional)
-- INSERT INTO audit_logs (user_id, role, action_type, entity_type, entity_id, description, ip_address) VALUES
-- (1, 'Super Admin', 'CREATE', 'QUESTION', 1, 'Created question: What is the capital of France?', '127.0.0.1'),
-- (1, 'Super Admin', 'CREATE', 'QUIZ', 1, 'Created quiz: General Knowledge Quiz', '127.0.0.1');

-- Show inserted data
SELECT 'Roles inserted:' as info, COUNT(*) as count FROM roles;
SELECT 'Users inserted:' as info, COUNT(*) as count FROM users;
SELECT 'Questions inserted:' as info, COUNT(*) as count FROM questions;
SELECT 'Question options inserted:' as info, COUNT(*) as count FROM question_options;
SELECT 'Quizzes inserted:' as info, COUNT(*) as count FROM quizzes;
SELECT 'Quiz questions inserted:' as info, COUNT(*) as count FROM quiz_questions;
SELECT 'Quiz assignments inserted:' as info, COUNT(*) as count FROM quiz_assignments;
SELECT 'Magic links inserted:' as info, COUNT(*) as count FROM magic_links;
SELECT 'Audit logs inserted:' as info, COUNT(*) as count FROM audit_logs;
