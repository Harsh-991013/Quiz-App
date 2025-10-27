CREATE DATABASE IF NOT EXISTS `quiz_app` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `quiz_app`;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'SUPERADMIN', 'CANDIDATE') NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create questions table
CREATE TABLE IF NOT EXISTS questions (
    id VARCHAR(36) PRIMARY KEY,
    question_text TEXT NOT NULL,
    correct_answer VARCHAR(500) NOT NULL,
    category VARCHAR(100) NOT NULL,
    difficulty ENUM('EASY', 'MEDIUM', 'HARD') NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create question_options table
CREATE TABLE IF NOT EXISTS question_options (
    id VARCHAR(36) PRIMARY KEY,
    question_id VARCHAR(36) NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    option_order INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Create quizzes table
CREATE TABLE IF NOT EXISTS quizzes (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date_time TIMESTAMP NOT NULL,
    end_date_time TIMESTAMP NOT NULL,
    duration INT NOT NULL,
    randomize_questions BOOLEAN NOT NULL DEFAULT FALSE,
    randomize_answers BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create quiz_questions table
CREATE TABLE IF NOT EXISTS quiz_questions (
    id VARCHAR(36) PRIMARY KEY,
    quiz_id VARCHAR(36) NOT NULL,
    question_id VARCHAR(36) NOT NULL,
    question_order INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

-- Create quiz_assignments table
CREATE TABLE IF NOT EXISTS quiz_assignments (
    id VARCHAR(36) PRIMARY KEY,
    quiz_id VARCHAR(36) NOT NULL,
    candidate_email VARCHAR(255) NOT NULL,
    status ENUM('PENDING', 'STARTED', 'COMPLETED', 'EXPIRED') NOT NULL DEFAULT 'PENDING',
    assigned_at TIMESTAMP NOT NULL,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);

-- Create magic_links table
CREATE TABLE IF NOT EXISTS magic_links (
    id VARCHAR(36) PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    quiz_assignment_id VARCHAR(36) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    used_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_assignment_id) REFERENCES quiz_assignments(id) ON DELETE CASCADE
);

-- Create audit_logs table
CREATE TABLE IF NOT EXISTS audit_logs (
    id VARCHAR(36) PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    resource_id VARCHAR(36),
    description TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default admin users
INSERT IGNORE INTO users (id, email, first_name, last_name, role, active) VALUES
('admin-uuid-1', 'admin@quiz.com', 'Admin', 'User', 'ADMIN', TRUE),
('superadmin-uuid-1', 'superadmin@quiz.com', 'Super', 'Admin', 'SUPERADMIN', TRUE);

-- Insert sample questions
INSERT IGNORE INTO questions (id, question_text, correct_answer, category, difficulty) VALUES
('q1-uuid', 'What is the capital of France?', 'Paris', 'Geography', 'EASY'),
('q2-uuid', 'What is 2 + 2?', '4', 'Mathematics', 'EASY'),
('q3-uuid', 'What is the largest planet in our solar system?', 'Jupiter', 'Science', 'MEDIUM'),
('q4-uuid', 'What does HTML stand for?', 'HyperText Markup Language', 'Computer Science', 'MEDIUM'),
('q5-uuid', 'What is the time complexity of binary search?', 'O(log n)', 'Algorithms', 'HARD');

-- Insert sample question options
INSERT IGNORE INTO question_options (id, question_id, option_text, option_order) VALUES
-- Question 1 options
('opt1-1', 'q1-uuid', 'London', 1),
('opt1-2', 'q1-uuid', 'Berlin', 2),
('opt1-3', 'q1-uuid', 'Paris', 3),
('opt1-4', 'q1-uuid', 'Madrid', 4),
-- Question 2 options
('opt2-1', 'q2-uuid', '3', 1),
('opt2-2', 'q2-uuid', '4', 2),
('opt2-3', 'q2-uuid', '5', 3),
('opt2-4', 'q2-uuid', '6', 4),
-- Question 3 options
('opt3-1', 'q3-uuid', 'Earth', 1),
('opt3-2', 'q3-uuid', 'Jupiter', 2),
('opt3-3', 'q3-uuid', 'Saturn', 3),
('opt3-4', 'q3-uuid', 'Neptune', 4),
-- Question 4 options
('opt4-1', 'q4-uuid', 'HyperText Markup Language', 1),
('opt4-2', 'q4-uuid', 'High Tech Modern Language', 2),
('opt4-3', 'q4-uuid', 'Home Tool Markup Language', 3),
('opt4-4', 'q4-uuid', 'Hyperlink and Text Markup Language', 4),
-- Question 5 options
('opt5-1', 'q5-uuid', 'O(n)', 1),
('opt5-2', 'q5-uuid', 'O(log n)', 2),
('opt5-3', 'q5-uuid', 'O(n log n)', 3),
('opt5-4', 'q5-uuid', 'O(1)', 4);

-- Insert sample quiz
INSERT IGNORE INTO quizzes (id, name, description, start_date_time, end_date_time, duration, randomize_questions, randomize_answers, active) VALUES
('quiz-uuid-1', 'Sample Quiz - Magic Link Demo', 'A sample quiz for testing magic link functionality', 
 DATE_ADD(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 3 HOUR), 30, FALSE, FALSE, TRUE);

-- Insert quiz questions
INSERT IGNORE INTO quiz_questions (id, quiz_id, question_id, question_order) VALUES
('qq1-uuid', 'quiz-uuid-1', 'q1-uuid', 1),
('qq2-uuid', 'quiz-uuid-1', 'q2-uuid', 2),
('qq3-uuid', 'quiz-uuid-1', 'q3-uuid', 3);

-- Create indexes for better performance (MySQL does not support CREATE INDEX IF NOT EXISTS)
-- Helper procedure to create an index only if it does not already exist
DELIMITER //
CREATE PROCEDURE create_index_if_missing(IN tbl VARCHAR(64), IN idx VARCHAR(64), IN stmt TEXT)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = tbl
          AND INDEX_NAME = idx
    ) THEN
        SET @sql := stmt;
        PREPARE s FROM @sql;
        EXECUTE s;
        DEALLOCATE PREPARE s;
    END IF;
END//
DELIMITER ;

CALL create_index_if_missing('users','idx_users_email','CREATE INDEX idx_users_email ON users(email)');
CALL create_index_if_missing('questions','idx_questions_category','CREATE INDEX idx_questions_category ON questions(category)');
CALL create_index_if_missing('questions','idx_questions_difficulty','CREATE INDEX idx_questions_difficulty ON questions(difficulty)');
CALL create_index_if_missing('questions','idx_questions_deleted','CREATE INDEX idx_questions_deleted ON questions(deleted)');
CALL create_index_if_missing('quizzes','idx_quizzes_active','CREATE INDEX idx_quizzes_active ON quizzes(active)');
CALL create_index_if_missing('quiz_assignments','idx_quiz_assignments_email','CREATE INDEX idx_quiz_assignments_email ON quiz_assignments(candidate_email)');
CALL create_index_if_missing('quiz_assignments','idx_quiz_assignments_status','CREATE INDEX idx_quiz_assignments_status ON quiz_assignments(status)');
CALL create_index_if_missing('magic_links','idx_magic_links_token','CREATE INDEX idx_magic_links_token ON magic_links(token)');
CALL create_index_if_missing('magic_links','idx_magic_links_expires','CREATE INDEX idx_magic_links_expires ON magic_links(expires_at)');
CALL create_index_if_missing('magic_links','idx_magic_links_used','CREATE INDEX idx_magic_links_used ON magic_links(used)');
CALL create_index_if_missing('audit_logs','idx_audit_logs_user','CREATE INDEX idx_audit_logs_user ON audit_logs(user_email)');
CALL create_index_if_missing('audit_logs','idx_audit_logs_action','CREATE INDEX idx_audit_logs_action ON audit_logs(action)');
CALL create_index_if_missing('audit_logs','idx_audit_logs_created','CREATE INDEX idx_audit_logs_created ON audit_logs(created_at)');

-- Drop helper procedure to keep schema clean
DROP PROCEDURE IF EXISTS create_index_if_missing;

-- Show success message
SELECT 'Database initialization completed successfully!' as message;
