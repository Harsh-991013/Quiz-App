-- ===========================================
-- COMPLETE DATABASE RESET & FRESH DATA SETUP
-- ===========================================

-- Disable foreign key checks to allow deletion
SET FOREIGN_KEY_CHECKS = 0;

-- Delete all existing data in reverse dependency order
DELETE FROM audit_logs;
DELETE FROM scores;
DELETE FROM attempt_answers;
DELETE FROM quiz_attempts;
DELETE FROM quiz_assignments;
DELETE FROM quiz_questions;
DELETE FROM question_options;
DELETE FROM questions;
DELETE FROM quizzes;
DELETE FROM users;
DELETE FROM roles;
DELETE FROM question_categories;
DELETE FROM difficulty_levels;
DELETE FROM question_types;

-- Delete new monitoring tables
DELETE FROM quiz_warnings;
DELETE FROM quiz_violations;
DELETE FROM notifications;
DELETE FROM quiz_policies;

-- Delete all lookup tables
DELETE FROM assignment_statuses;
DELETE FROM policy_statuses;
DELETE FROM notification_types;
DELETE FROM delivery_statuses;
DELETE FROM policy_violation_types;
DELETE FROM violation_actions;
DELETE FROM auto_submit_reasons;
DELETE FROM violation_severities;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- ===========================================
-- INSERT FRESH TEST DATA FOR ALL TABLES
-- ===========================================

-- 1. Roles
INSERT INTO roles (role_id, role_name, description) VALUES
(1, 'Admin', 'Administrator with full access'),
(2, 'Candidate', 'User taking quizzes');

-- 2. Users
INSERT INTO users (user_id, role_id, full_name, email, password_hash, status, created_at, updated_at) VALUES
(1, 1, 'Admin User', 'admin@testmian.com', 'HASHED_ADMIN_PASS', 'Active', NOW(), NOW()),
(2, 2, 'Alice Johnson', 'alice@testmian.com', 'HASHED_PASS', 'Active', NOW(), NOW()),
(3, 2, 'Bob Smith', 'bob@testmian.com', 'HASHED_PASS', 'Active', NOW(), NOW()),
(4, 2, 'Charlie Brown', 'charlie@testmian.com', 'HASHED_PASS', 'Active', NOW(), NOW()),
(5, 2, 'Diana Prince', 'diana@testmian.com', 'HASHED_PASS', 'Active', NOW(), NOW());

-- 3. Question Categories
INSERT INTO question_categories (category_id, category_name, description, created_by) VALUES
(1, 'Java Basics', 'Covers basic Java concepts', 1),
(2, 'Java Advanced', 'Advanced Java topics', 1),
(3, 'Database', 'Database concepts', 1);

-- 4. Difficulty Levels
INSERT INTO difficulty_levels (difficulty_id, difficulty_key, display_name) VALUES
(1, 'easy', 'Easy'),
(2, 'medium', 'Medium'),
(3, 'hard', 'Hard');

-- 5. Question Types
INSERT INTO question_types (question_type_id, type_key, display_name) VALUES
(1, 'mcq', 'Multiple Choice Question');

-- 6. Questions (10 questions total)
INSERT INTO questions (question_id, category_id, question_type_id, difficulty_id, question_text, created_by) VALUES
(1, 1, 1, 1, 'Which keyword is used to inherit a class in Java?', 1),
(2, 1, 1, 1, 'Which of these is not a Java primitive type?', 1),
(3, 1, 1, 2, 'What is the default value of a local variable?', 1),
(4, 1, 1, 2, 'Which method is called when an object is created?', 1),
(5, 1, 1, 1, 'Which of these is used to handle exceptions in Java?', 1),
(6, 2, 1, 2, 'Which of these collections guarantees no duplicates?', 1),
(7, 2, 1, 2, 'What is the parent class of all Java classes?', 1),
(8, 1, 1, 1, 'Which keyword is used to define a constant in Java?', 1),
(9, 3, 1, 1, 'Which package contains the Random class?', 1),
(10, 2, 1, 2, 'Which of these can be used for multi-threading?', 1);

-- 7. Question Options (4 options per question)
INSERT INTO question_options (option_id, question_id, option_text, is_correct, created_by) VALUES
-- Q1
(1,1,'extends',TRUE,1),
(2,1,'implements',FALSE,1),
(3,1,'inherits',FALSE,1),
(4,1,'instanceof',FALSE,1),
-- Q2
(5,2,'int',FALSE,1),
(6,2,'float',FALSE,1),
(7,2,'boolean',FALSE,1),
(8,2,'string',TRUE,1),
-- Q3
(9,3,'null',FALSE,1),
(10,3,'0',FALSE,1),
(11,3,'Depends on type',FALSE,1),
(12,3,'No default value',TRUE,1),
-- Q4
(13,4,'init()',FALSE,1),
(14,4,'main()',FALSE,1),
(15,4,'constructor',TRUE,1),
(16,4,'create()',FALSE,1),
-- Q5
(17,5,'try-catch',TRUE,1),
(18,5,'if-else',FALSE,1),
(19,5,'switch',FALSE,1),
(20,5,'loop',FALSE,1),
-- Q6
(21,6,'List',FALSE,1),
(22,6,'Set',TRUE,1),
(23,6,'ArrayList',FALSE,1),
(24,6,'Queue',FALSE,1),
-- Q7
(25,7,'Object',TRUE,1),
(26,7,'Parent',FALSE,1),
(27,7,'Superclass',FALSE,1),
(28,7,'BaseClass',FALSE,1),
-- Q8
(29,8,'const',FALSE,1),
(30,8,'final',TRUE,1),
(31,8,'static',FALSE,1),
(32,8,'constant',FALSE,1),
-- Q9
(33,9,'java.util',TRUE,1),
(34,9,'java.random',FALSE,1),
(35,9,'java.io',FALSE,1),
(36,9,'java.lang',FALSE,1),
-- Q10
(37,10,'Thread',TRUE,1),
(38,10,'Runnable',TRUE,1),
(39,10,'Executor',FALSE,1),
(40,10,'Timer',FALSE,1);

-- 8. Quizzes
INSERT INTO quizzes (quiz_id, title, description, duration_minutes, created_by, is_active, created_at, updated_at) VALUES
(1, 'Java Basics Quiz', 'Test your Java fundamentals', 20, 1, TRUE, NOW(), NOW()),
(2, 'Advanced Java Quiz', 'Advanced Java concepts', 30, 1, TRUE, NOW(), NOW());

-- 9. Quiz Questions (linking quiz to questions with marks)
INSERT INTO quiz_questions (id, quiz_id, question_id, marks) VALUES
(1,1,1,1.0),
(2,1,2,1.0),
(3,1,3,1.0),
(4,1,4,1.0),
(5,1,5,1.0),
(6,2,6,1.5),
(7,2,7,1.5),
(8,2,8,1.5),
(9,2,9,1.5),
(10,2,10,1.5);

-- 10. Assignment Statuses
INSERT INTO assignment_statuses (status_key, display_name, description) VALUES
('Assigned', 'Assigned', 'Quiz assigned to candidate'),
('Started', 'Started', 'Candidate has started the quiz'),
('Completed', 'Completed', 'Quiz completed successfully'),
('Auto_Submitted', 'Auto Submitted', 'Quiz auto-submitted due to timeout/violations'),
('Expired', 'Expired', 'Quiz link has expired');

-- 11. Policy Statuses (missing - required for quiz attempts)
INSERT INTO policy_statuses (status_key, display_name) VALUES
('Active', 'Active'),
('Suspended', 'Suspended'),
('Violated', 'Violated');

-- 11. Quiz Assignments (match entity - no created_at field, use assignment_status_id)
INSERT INTO quiz_assignments (assignment_id, quiz_id, candidate_id, unique_link, assignment_status_id, assigned_by, assigned_at, updated_at) VALUES
(1,1,2,'quiz-alice-001',1,1,NOW(),NOW()),
(2,1,3,'quiz-bob-001',1,1,NOW(),NOW()),
(3,2,4,'quiz-charlie-002',1,1,NOW(),NOW()),
(4,2,5,'quiz-diana-002',1,1,NOW(),NOW());

-- 12. Quiz Attempts (add required fields: violation_count, warning_count, auto_submitted, policy_status_id)
INSERT INTO quiz_attempts (attempt_id, assignment_id, start_time, auto_submitted, violation_count, warning_count, policy_status_id, created_at, updated_at) VALUES
(1,1,NOW(),FALSE,0,0,1,NOW(),NOW()),
(2,2,NOW(),FALSE,0,0,1,NOW(),NOW());

-- 13. Attempt Answers (sample answers)
INSERT INTO attempt_answers (answer_id, attempt_id, question_id, selected_option_id, is_correct, marks_obtained, answered_at) VALUES
(1,1,1,1,TRUE,1.0,NOW()),
(2,1,2,8,TRUE,1.0,NOW()),
(3,2,1,2,FALSE,0.0,NOW());

-- 14. Scores (match entity: attempt_id, candidate_id, candidate_name, total_questions, correct_answers, score, created_at)
INSERT INTO scores (score_id, attempt_id, candidate_id, candidate_name, total_questions, correct_answers, score, created_at) VALUES
(1,1,2,'Alice Johnson',5,2,2.0,NOW()),
(2,2,3,'Bob Smith',5,0,0.0,NOW());

-- 15. Notification Types
INSERT INTO notification_types (type_key, display_name) VALUES
('QUIZ_START', 'Quiz Started'),
('QUIZ_COMPLETE', 'Quiz Completed'),
('QUIZ_AUTO_SUBMIT', 'Quiz Auto-Submitted'),
('VIOLATION_WARNING', 'Violation Warning');

-- 16. Delivery Statuses
INSERT INTO delivery_statuses (status_key, display_name) VALUES
('Pending', 'Pending'),
('Sent', 'Sent'),
('Delivered', 'Delivered'),
('Failed', 'Failed');

-- 17. Notifications
INSERT INTO notifications (notification_id, recipient_id, notification_type_id, delivery_status_id, subject, message, sent_at, created_at) VALUES
(1,2,1,2,'Quiz Started: Java Basics Quiz','You have started the quiz: Java Basics Quiz',NOW(),NOW()),
(2,3,1,2,'Quiz Started: Java Basics Quiz','You have started the quiz: Java Basics Quiz',NOW(),NOW());

-- 12. Violation Severities (must be inserted first due to foreign key)
INSERT INTO violation_severities (severity_id, severity_key, display_name, description) VALUES
(1, 'LOW', 'Low', 'Minor violation that requires only a warning. Examples: browser minimize, right-click menu access. These are typically UI interactions that may be accidental.'),
(2, 'MEDIUM', 'Medium', 'Moderate violation that needs closer monitoring. Examples: tab switching, basic copy operations. These indicate potential cheating behavior that should be tracked.'),
(3, 'HIGH', 'High', 'Serious violation that may trigger auto-submit depending on policy. Examples: copy/paste operations, developer tools access. These are clear indicators of cheating attempts.'),
(4, 'CRITICAL', 'Critical', 'Severe violation requiring immediate auto-submit. Examples: multi-device access, quiz timeout. These violations pose significant security risks and require immediate termination.');

-- 18. Policy Violation Types (now with severity_id)
INSERT INTO policy_violation_types (violation_type_id, violation_key, display_name, description, severity_id) VALUES
(1, 'TAB_SWITCH', 'Tab Switching', 'Candidate switched browser tabs', 2),
(2, 'BROWSER_MINIMIZE', 'Browser Minimized', 'Browser window was minimized', 1),
(3, 'MULTI_DEVICE', 'Multiple Devices', 'Quiz opened on multiple devices', 4),
(4, 'COPY_PASTE', 'Copy/Paste Detected', 'Copy or paste operation detected', 3),
(5, 'RIGHT_CLICK', 'Right Click', 'Right-click context menu accessed', 1),
(6, 'DEV_TOOLS', 'Developer Tools', 'Browser developer tools opened', 3),
(7, 'TIME_WARNING', 'Time Warning', 'Timer warning issued', 1),
(8, 'QUIZ_TIMEOUT', 'Quiz Timeout', 'Quiz timed out and auto-submitted', 4);

-- 19. Violation Actions
INSERT INTO violation_actions (action_key, display_name) VALUES
('WARNING', 'Issue Warning'),
('BLOCK', 'Block Access'),
('AUTO_SUBMIT', 'Auto Submit');

-- 20. Auto Submit Reasons
INSERT INTO auto_submit_reasons (reason_key, display_name) VALUES
('TIME_UP', 'Time Expired'),
('MAX_VIOLATIONS', 'Maximum Violations'),
('SYSTEM_ERROR', 'System Error');

-- 21. Quiz Violations (match entity: attempt_id, candidate_id, violation_type_id, violation_description, timestamp, ip_address, device_info, is_critical, created_at)
INSERT INTO quiz_violations (violation_id, attempt_id, candidate_id, violation_type_id, violation_description, timestamp, ip_address, device_info, is_critical, created_at) VALUES
(1,1,2,1,'Tab switch detected',NOW(),'192.168.1.100','Chrome/Windows',FALSE,NOW()),
(2,2,3,2,'Browser minimized',NOW(),'192.168.1.101','Firefox/Linux',FALSE,NOW());

-- 22. Quiz Warnings (match entity: attempt_id, violation_id, candidate_id, message, warning_number, timestamp, is_final_warning, created_at)
INSERT INTO quiz_warnings (warning_id, attempt_id, violation_id, candidate_id, message, warning_number, timestamp, is_final_warning, created_at) VALUES
(1,1,1,2,'Warning: Tab switching detected',1,NOW(),FALSE,NOW()),
(2,2,2,3,'Warning: Browser minimization detected',1,NOW(),FALSE,NOW());

-- 23. Quiz Policies (match entity: quiz_id, max_warnings_before_auto_submit, auto_submit_on_multi_device, auto_submit_on_timer_expiry, created_by, created_at, updated_at)
INSERT INTO quiz_policies (policy_id, quiz_id, max_warnings_before_auto_submit, auto_submit_on_multi_device, auto_submit_on_timer_expiry, created_by, created_at, updated_at) VALUES
(1,1,3,TRUE,TRUE,1,NOW(),NOW()),
(2,2,2,TRUE,TRUE,1,NOW(),NOW());

-- 24. Audit Logs
INSERT INTO audit_logs (log_id, user_id, role, action_type, entity_type, entity_id, description, ip_address, device_info, session_id, old_value, new_value, created_at) VALUES
(1,2,'Candidate','QUIZ_START','QuizAttempt',1,'Started quiz attempt','192.168.1.100','Chrome','session123',NULL,NULL,NOW()),
(2,2,'Candidate','ANSWER_SUBMIT','Question',1,'Submitted answer for question 1','192.168.1.100','Chrome','session123',NULL,'1',NOW()),
(3,3,'Candidate','QUIZ_START','QuizAttempt',2,'Started quiz attempt','192.168.1.101','Firefox','session456',NULL,NULL,NOW()),
(4,1,'Admin','QUIZ_ASSIGNMENT','QuizAssignment',1,'Assigned quiz to candidate','127.0.0.1','Admin Panel','admin123',NULL,'Assigned',NOW());