# Updated Database Schema with BIGINT Columns

## Problem Solved
The original database schema used INT for ID columns, but Java entities use `Long` (BIGINT). I've modified the provided schema script to use BIGINT for all ID columns to match the entity definitions.

## Key Changes Made
- Changed all `INT` to `BIGINT` for primary key and foreign key columns
- Maintained all table structures and relationships
- Ensured compatibility with Hibernate/JPA entities

## Modified Database Creation Script

```sql
/*
Updated Database Schema with BIGINT Columns
*/

DROP DATABASE IF EXISTS testmian_quiz_app_v2;
CREATE DATABASE testmian_quiz_app_v2;
USE testmian_quiz_app_v2;


DROP TABLE IF EXISTS roles;
CREATE TABLE roles (
  role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_name VARCHAR(50) UNIQUE NOT NULL,
  description TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT,
  updated_at DATETIME,
  updated_by BIGINT
);

DROP TABLE IF EXISTS auth_methods;
CREATE TABLE auth_methods (
  auth_method_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  method_key VARCHAR(50) UNIQUE NOT NULL,
  display_name VARCHAR(100)
);

DROP TABLE IF EXISTS invite_statuses;
CREATE TABLE invite_statuses (
  invite_status_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  status_key VARCHAR(50) UNIQUE NOT NULL,
  display_name VARCHAR(100)
);

DROP TABLE IF EXISTS user_statuses;
CREATE TABLE user_statuses (
  status_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  status_key VARCHAR(50) UNIQUE NOT NULL,
  display_name VARCHAR(100)
);

DROP TABLE IF EXISTS magic_link_purposes;
CREATE TABLE magic_link_purposes (
  purpose_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  purpose_key VARCHAR(50) UNIQUE NOT NULL,
  description VARCHAR(100)
);

DROP TABLE IF EXISTS delivery_statuses;
CREATE TABLE delivery_statuses (
  delivery_status_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  status_key VARCHAR(50) UNIQUE NOT NULL,
  display_name VARCHAR(100)
);

DROP TABLE IF EXISTS assignment_statuses;
CREATE TABLE assignment_statuses (
  assignment_status_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  status_key VARCHAR(50) UNIQUE NOT NULL,
  display_name VARCHAR(100)
);

DROP TABLE IF EXISTS notification_types;
CREATE TABLE notification_types (
  notification_type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type_key VARCHAR(50) UNIQUE NOT NULL,
  display_name VARCHAR(100)
);


DROP TABLE IF EXISTS users;
CREATE TABLE users (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  role_id BIGINT NOT NULL,
  full_name VARCHAR(100),
  email VARCHAR(150) UNIQUE NOT NULL,
  password_hash VARCHAR(255),
  auth_method ENUM('password','magic_link','oauth') DEFAULT 'magic_link',
  is_invited TINYINT(1) DEFAULT 0,
  invited_by BIGINT,
  invite_status ENUM('Pending','Activated','Expired') DEFAULT 'Pending',
  status ENUM('Active','Inactive','Suspended') DEFAULT 'Inactive',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by VARCHAR(50),
  updated_at DATETIME,
  updated_by VARCHAR(50),
  deleted_at DATETIME,
  deleted_by BIGINT,
  FOREIGN KEY (role_id) REFERENCES roles(role_id),
  FOREIGN KEY (invited_by) REFERENCES users(user_id),
  FOREIGN KEY (deleted_by) REFERENCES users(user_id)
);

DROP TABLE IF EXISTS sessions;
CREATE TABLE sessions (
  session_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  jwt_token TEXT,
  issued_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  expires_at DATETIME NOT NULL,
  last_activity_at DATETIME,
  is_active BOOLEAN DEFAULT TRUE,
  device_info VARCHAR(255),
  ip_address VARCHAR(50),
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

DROP TABLE IF EXISTS magic_links;
CREATE TABLE magic_links (
  magic_link_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  token_hash VARCHAR(255) UNIQUE,
  purpose_id BIGINT NOT NULL DEFAULT 2,
  expires_at DATETIME NOT NULL,
  used BOOLEAN DEFAULT FALSE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  used_at DATETIME,
  ip_address VARCHAR(50),
  delivery_status_id BIGINT DEFAULT 1,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (purpose_id) REFERENCES magic_link_purposes(purpose_id),
  FOREIGN KEY (delivery_status_id) REFERENCES delivery_statuses(delivery_status_id)
);


DROP TABLE IF EXISTS question_categories;
CREATE TABLE question_categories (
  category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(100) UNIQUE NOT NULL,
  description TEXT,
  created_by BIGINT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME,
  deleted_at DATETIME,
  updated_by BIGINT,
  deleted_by BIGINT,
  FOREIGN KEY (created_by) REFERENCES users(user_id),
  FOREIGN KEY (updated_by) REFERENCES users(user_id),
  FOREIGN KEY (deleted_by) REFERENCES users(user_id)
);

DROP TABLE IF EXISTS difficulty_levels;
CREATE TABLE difficulty_levels (
  difficulty_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  difficulty_key VARCHAR(50) UNIQUE NOT NULL,
  display_name VARCHAR(50) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS question_types;
CREATE TABLE question_types (
  question_type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type_key VARCHAR(50) UNIQUE NOT NULL,
  display_name VARCHAR(100) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);


DROP TABLE IF EXISTS questions;
CREATE TABLE questions (
  question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_id BIGINT NOT NULL,
  question_type_id BIGINT NOT NULL,
  difficulty_id BIGINT NOT NULL,
  question_text TEXT NOT NULL,
  created_by BIGINT,
  is_active BOOLEAN DEFAULT TRUE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME,
  deleted_at DATETIME,
  updated_by BIGINT,
  deleted_by BIGINT,
  FOREIGN KEY (category_id) REFERENCES question_categories(category_id),
  FOREIGN KEY (question_type_id) REFERENCES question_types(question_type_id),
  FOREIGN KEY (difficulty_id) REFERENCES difficulty_levels(difficulty_id),
  FOREIGN KEY (created_by) REFERENCES users(user_id),
  FOREIGN KEY (updated_by) REFERENCES users(user_id),
  FOREIGN KEY (deleted_by) REFERENCES users(user_id)
);

DROP TABLE IF EXISTS question_options;
CREATE TABLE question_options (
  option_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  question_id BIGINT NOT NULL,
  option_text TEXT NOT NULL,
  is_correct BOOLEAN DEFAULT FALSE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME,
  created_by BIGINT,
  updated_by BIGINT,
  deleted_by BIGINT,
  deleted_at DATETIME,
  FOREIGN KEY (question_id) REFERENCES questions(question_id),
  FOREIGN KEY (created_by) REFERENCES users(user_id),
  FOREIGN KEY (updated_by) REFERENCES users(user_id),
  FOREIGN KEY (deleted_by) REFERENCES users(user_id)
);


DROP TABLE IF EXISTS quizzes;
CREATE TABLE quizzes (
  quiz_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(150) NOT NULL,
  description TEXT,
  duration_minutes INT,
  difficulty_id BIGINT,
  created_by BIGINT NOT NULL,
  is_active BOOLEAN DEFAULT TRUE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME,
  deleted_at DATETIME,
  updated_by BIGINT,
  deleted_by BIGINT,
  FOREIGN KEY (difficulty_id) REFERENCES difficulty_levels(difficulty_id),
  FOREIGN KEY (created_by) REFERENCES users(user_id),
  FOREIGN KEY (updated_by) REFERENCES users(user_id),
  FOREIGN KEY (deleted_by) REFERENCES users(user_id)
);

DROP TABLE IF EXISTS quiz_questions;
CREATE TABLE quiz_questions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  quiz_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  marks FLOAT DEFAULT 1,
  FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id),
  FOREIGN KEY (question_id) REFERENCES questions(question_id)
);


DROP TABLE IF EXISTS quiz_assignments;
CREATE TABLE quiz_assignments (
  assignment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  quiz_id BIGINT NOT NULL,
  candidate_id BIGINT NOT NULL,
  unique_link VARCHAR(255) UNIQUE NOT NULL,
  expires_at DATETIME,
  assignment_status_id BIGINT DEFAULT 1,
  assigned_by BIGINT,
  assigned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME,
  deleted_at DATETIME,
  updated_by BIGINT,
  deleted_by BIGINT,
  FOREIGN KEY (quiz_id) REFERENCES quizzes(quiz_id),
  FOREIGN KEY (candidate_id) REFERENCES users(user_id),
  FOREIGN KEY (assignment_status_id) REFERENCES assignment_statuses(assignment_status_id),
  FOREIGN KEY (assigned_by) REFERENCES users(user_id)
);

DROP TABLE IF EXISTS quiz_attempts;
CREATE TABLE quiz_attempts (
  attempt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  assignment_id BIGINT NOT NULL,
  session_id BIGINT,
  start_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  end_time DATETIME,
  total_score FLOAT,
  auto_submitted BOOLEAN DEFAULT FALSE,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME,
  FOREIGN KEY (assignment_id) REFERENCES quiz_assignments(assignment_id),
  FOREIGN KEY (session_id) REFERENCES sessions(session_id)
);

DROP TABLE IF EXISTS attempt_answers;
CREATE TABLE attempt_answers (
  answer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  attempt_id BIGINT NOT NULL,
  question_id BIGINT NOT NULL,
  selected_option_id BIGINT,
  is_correct BOOLEAN,
  marks_obtained FLOAT,
  answered_at DATETIME,
  FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(attempt_id),
  FOREIGN KEY (question_id) REFERENCES questions(question_id),
  FOREIGN KEY (selected_option_id) REFERENCES question_options(option_id)
);

DROP TABLE IF EXISTS scores;
CREATE TABLE scores (
  score_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  attempt_id BIGINT NOT NULL,
  category_id BIGINT,
  candidate_id BIGINT NOT NULL,
  candidate_name VARCHAR(150),
  total_questions INT,
  correct_answers INT,
  score FLOAT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  created_by BIGINT,
  FOREIGN KEY (attempt_id) REFERENCES quiz_attempts(attempt_id),
  FOREIGN KEY (category_id) REFERENCES question_categories(category_id),
  FOREIGN KEY (candidate_id) REFERENCES users(user_id),
  FOREIGN KEY (created_by) REFERENCES users(user_id)
);



DROP TABLE IF EXISTS notifications;
CREATE TABLE notifications (
  notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  recipient_id BIGINT NOT NULL,
  notification_type_id BIGINT NOT NULL,
  message TEXT,
  delivery_status_id BIGINT DEFAULT 1,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (recipient_id) REFERENCES users(user_id),
  FOREIGN KEY (notification_type_id) REFERENCES notification_types(notification_type_id),
  FOREIGN KEY (delivery_status_id) REFERENCES delivery_statuses(delivery_status_id)
);

DROP TABLE IF EXISTS audit_logs;
CREATE TABLE audit_logs (
  log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT,
  role VARCHAR(50) NOT NULL,
  action_type VARCHAR(100) NOT NULL,
  entity_type VARCHAR(100),
  entity_id BIGINT,
  description TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  ip_address VARCHAR(50),
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

## Alternative Approach (If Database Recreation is Preferred)

If you prefer to drop and recreate the database:

```sql
-- Drop and recreate database (WARNING: This will delete all data)
DROP DATABASE IF EXISTS testmian_quiz_app;
CREATE DATABASE testmian_quiz_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE testmian_quiz_app;

-- Then run your init.sql script
SOURCE /path/to/init.sql;
```

## Execution Instructions

1. **Backup your database first** (recommended even for test data)
2. Connect to MySQL: `mysql -u root -p`
3. Run the migration script above
4. Restart your Spring Boot application
5. Verify that Hibernate no longer shows schema update errors

## Verification

After running the migration:
- Check that all tables have BIGINT columns for IDs
- Verify foreign key constraints are intact
- Ensure AUTO_INCREMENT works correctly
- Test application startup without errors

## Notes

- This migration assumes you have test data that can be recreated
- If you have production data, consider using Flyway or Liquibase for versioned migrations
- Always backup before running schema changes
- Test thoroughly in a development environment first