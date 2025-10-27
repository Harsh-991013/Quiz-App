-- Test data for testmain_quiz_app_v2 database
-- Run this script after creating the database schema

USE testmian_quiz_app_v2;

-- Insert roles
INSERT INTO roles (role_name, description) VALUES
('SUPER_ADMIN', 'Super Administrator with full access'),
('ADMIN', 'Administrator with management access'),
('CANDIDATE', 'Quiz candidate');

-- Insert auth methods
INSERT INTO auth_methods (method_key, display_name) VALUES
('PASSWORD', 'Password Authentication'),
('MAGIC_LINK', 'Magic Link Authentication');

-- Insert invite statuses
INSERT INTO invite_statuses (status_key, display_name) VALUES
('PENDING', 'Invitation Pending'),
('ACCEPTED', 'Invitation Accepted'),
('EXPIRED', 'Invitation Expired');

-- Insert user statuses
INSERT INTO user_statuses (status_key, display_name) VALUES
('INACTIVE', 'User Inactive'),
('ACTIVE', 'User Active'),
('SUSPENDED', 'User Suspended');

-- Insert magic link purposes
INSERT INTO magic_link_purposes (purpose_key, description) VALUES
('ACTIVATE_ADMIN', 'Admin activation'),
('LOGIN', 'User login'),
('REGISTRATION', 'User registration'),
('PASSWORD_RESET', 'Password reset');

-- Insert delivery statuses
INSERT INTO delivery_statuses (status_key, display_name) VALUES
('PENDING', 'Delivery Pending'),
('SENT', 'Delivered Successfully'),
('FAILED', 'Delivery Failed');

-- Insert assignment statuses
INSERT INTO assignment_statuses (status_key, display_name) VALUES
('ASSIGNED', 'Quiz Assigned'),
('STARTED', 'Quiz Started'),
('COMPLETED', 'Quiz Completed'),
('EXPIRED', 'Assignment Expired');

-- Insert notification types
INSERT INTO notification_types (type_key, display_name) VALUES
('QUIZ_ASSIGNED', 'Quiz Assignment Notification'),
('QUIZ_COMPLETED', 'Quiz Completion Notification'),
('SYSTEM_ALERT', 'System Alert');

-- Insert users (passwords are '123456' hashed with BCrypt)
INSERT INTO users (role_id, full_name, email, password_hash, auth_method, invite_status, status) VALUES
(1, 'Himanshu', 'superadmin@test.com', '$2a$10$TKh8H1.PfQx37YgCzwiKb.K2IxwC0jz14tH1OUl8mgYyURZiWcmu', 'password', 'Activated', 'Active'),
(2, 'Manoj', 'admin@test.com', '$2a$10$TKh8H1.PfQx37YgCzwiKb.K2IxwC0jz14tH1OUl8mgYyURZiWcmu', 'password', 'Activated', 'Active'),
(3, 'John Doe', 'candidate@test.com', '$2a$10$TKh8H1.PfQx37YgCzwiKb.K2IxwC0jz14tH1OUl8mgYyURZiWcmu', 'password', 'Activated', 'Active');

-- Insert difficulty levels
INSERT INTO difficulty_levels (difficulty_key, display_name) VALUES
('EASY', 'Easy'),
('MEDIUM', 'Medium'),
('HARD', 'Hard');

-- Insert question types
INSERT INTO question_types (type_key, display_name) VALUES
('MULTIPLE_CHOICE', 'Multiple Choice'),
('TRUE_FALSE', 'True/False'),
('SHORT_ANSWER', 'Short Answer');

-- Insert question categories
INSERT INTO question_categories (category_name, description, created_by) VALUES
('General Knowledge', 'General knowledge questions', 1),
('Programming', 'Programming related questions', 1),
('Mathematics', 'Mathematics questions', 1);

-- Insert questions
INSERT INTO questions (category_id, question_type_id, difficulty_id, question_text, created_by) VALUES
(1, 1, 1, 'What is the capital of France?', 1),
(2, 1, 2, 'What does HTML stand for?', 1),
(3, 1, 2, 'What is 2 + 2?', 1),
(1, 2, 1, 'The Earth is round. True or False?', 1),
(2, 1, 3, 'Which programming language is known for its use in web development and has a mascot named Guido?', 1);

-- Insert question options
INSERT INTO question_options (question_id, option_text, is_correct, created_by) VALUES
-- Question 1 options
(1, 'Paris', TRUE, 1),
(1, 'London', FALSE, 1),
(1, 'Berlin', FALSE, 1),
(1, 'Madrid', FALSE, 1),
-- Question 2 options
(2, 'HyperText Markup Language', TRUE, 1),
(2, 'High Tech Modern Language', FALSE, 1),
(2, 'Home Tool Management Language', FALSE, 1),
(2, 'Hyper Transfer Markup Language', FALSE, 1),
-- Question 3 options
(3, '4', TRUE, 1),
(3, '5', FALSE, 1),
(3, '3', FALSE, 1),
(3, '6', FALSE, 1),
-- Question 4 options (True/False)
(4, 'True', TRUE, 1),
(4, 'False', FALSE, 1),
-- Question 5 options
(5, 'Python', TRUE, 1),
(5, 'Java', FALSE, 1),
(5, 'JavaScript', FALSE, 1),
(5, 'C++', FALSE, 1);

-- Insert quizzes
INSERT INTO quizzes (title, description, duration_minutes, difficulty_id, created_by) VALUES
('Sample Quiz', 'A sample quiz for testing purposes', 30, 2, 1),
('General Knowledge Test', 'Test your general knowledge', 20, 1, 1),
('Programming Basics', 'Basic programming concepts quiz', 45, 2, 1);

-- Insert quiz questions
INSERT INTO quiz_questions (quiz_id, question_id, marks) VALUES
(1, 1, 1.0),
(1, 2, 1.0),
(1, 3, 1.0),
(2, 1, 2.0),
(2, 4, 2.0),
(3, 2, 3.0),
(3, 5, 3.0);