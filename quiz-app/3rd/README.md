# Quiz Management Microservice

A Spring Boot microservice for managing quizzes with Eureka service discovery, MySQL database, Kafka messaging, and email notifications.

## Features

### FR-04: Build and Schedule Quizzes
- **Quiz Creation**: Admin can create quizzes with name, description, duration, and select questions
- **Quiz Scheduling**: Define start/end times and assign to candidates or groups
- **Randomization**: Question and answer option randomization to prevent cheating
- **Secure Links**: Unique, time-bound quiz access links via email
- **Security & Logging**: Role-based permissions and comprehensive audit logging
- **Soft Delete**: Quizzes can be soft deleted and restored

## Architecture

### Microservices
- **Eureka Server**: Service discovery (Port: 8761)
- **Quiz Service**: Main quiz management service (Port: 8080)

### Technologies
- **Spring Boot 3.2.0**: Framework
- **Spring Cloud**: Service discovery with Eureka
- **Spring Data JPA**: Database access
- **MySQL**: Database
- **Spring Kafka**: Message processing
- **Spring Mail**: Email notifications
- **Thymeleaf**: Email templates
- **Spring Security**: Authentication & authorization
- **JWT**: Token-based authentication

## Prerequisites

- Java 21+
- MySQL 8.0+
- Apache Kafka
- Maven 3.6+

## Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE quiz_app;
```

2. Update database credentials in `quiz-service/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    username: root
    password: root
```

3. Run the database schema from `database/self-contained file/testmian_quiz_app_main.sql`

## Configuration

### Environment Variables
Set the following environment variables for email configuration:
```bash
export SMTP_USERNAME=your-email@gmail.com
export SMTP_PASSWORD=your-app-password
```

### Kafka Setup
Ensure Kafka is running on `localhost:9092`. Use the following docker command:
```bash
docker run -d --name kafka -p 9092:9092 -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 -e KAFKA_LISTENERS=PLAINTEXT://0.0.0.0:9092 apache/kafka:latest
```

## Running the Application

### 1. Start Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```
Access Eureka dashboard: http://localhost:8761

### 2. Start Quiz Service
```bash
cd quiz-service
mvn spring-boot:run
```

### 3. Verify Services
- Quiz Service: http://localhost:8080
- Eureka Dashboard: http://localhost:8761

## API Endpoints

### Authentication
- `POST /auth/magic-link` - Request magic link for login
- `GET /auth/magic-link?token={token}` - Validate magic link

### Quiz Management (Admin/SuperAdmin)
- `POST /api/quizzes` - Create quiz
- `GET /api/quizzes` - Get all quizzes
- `GET /api/quizzes/my` - Get my created quizzes
- `GET /api/quizzes/{id}` - Get quiz by ID
- `PUT /api/quizzes/{id}` - Update quiz
- `DELETE /api/quizzes/{id}` - Soft delete quiz
- `POST /api/quizzes/{id}/restore` - Restore deleted quiz

### Quiz Assignment
- `POST /api/quizzes/{quizId}/assign/{candidateId}` - Assign quiz to candidate
- `GET /api/quizzes/{quizId}/assignments` - Get quiz assignments
- `GET /api/candidate/assignments` - Get my assignments
- `GET /api/candidate/assignments/{uniqueLink}` - Get assignment by link

## Testing with Postman

1. Import the Postman collection from `quiz-service/src/main/resources/postman_collection.json`
2. Set environment variables:
   - `base_url`: http://localhost:8080
   - `admin_token`: JWT token for admin user
   - `superadmin_token`: JWT token for superadmin user
   - `candidate_token`: JWT token for candidate user

## Code Flow

### Quiz Creation Flow
1. Admin authenticates via magic link
2. Admin creates quiz with questions via `POST /api/quizzes`
3. System validates question count (max 100)
4. Questions are stored in `quiz_questions` table
5. Audit log is created

### Quiz Assignment Flow
1. Admin assigns quiz to candidate via `POST /api/quizzes/{id}/assign/{candidateId}`
2. Unique link is generated and stored in `quiz_assignments`
3. Email notification is sent via Kafka to email service
4. Candidate receives email with quiz link
5. Candidate accesses quiz via unique link

### Randomization Flow
1. When quiz is accessed, `QuizRandomizationService` randomizes questions
2. Questions are shuffled within the quiz
3. Answer options are randomized for each question
4. Randomized order is presented to candidate

### Soft Delete Flow
1. Admin deletes quiz via `DELETE /api/quizzes/{id}`
2. `deleted_at` timestamp is set in database
3. Quiz is marked as inactive
4. SuperAdmin can restore via `POST /api/quizzes/{id}/restore`

## Security Features

- **Role-based Access Control**: ADMIN, SUPERADMIN, CANDIDATE roles
- **JWT Authentication**: Token-based authentication
- **Magic Links**: Secure, expirable login links
- **Audit Logging**: All actions are logged with user details
- **Soft Delete**: Data preservation with logical deletion
- **Unique Links**: One-time use quiz access links

## Email Templates

Email templates are located in `src/main/resources/templates/`:
- `magic-link-email.html` - Magic link authentication
- `quiz-assignment-email.html` - Quiz assignment notifications

## Monitoring

- **Spring Boot Actuator**: Health checks and metrics
- **Eureka Dashboard**: Service registry monitoring
- **Audit Logs**: Comprehensive action logging

## Development

### Project Structure
```
quiz-microservice/
├── pom.xml
├── eureka-server/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/eureka/
│       └── resources/
├── quiz-service/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/quiz/
│       │   ├── controller/
│       │   ├── entity/
│       │   ├── repository/
│       │   └── service/
│       ├── resources/
│       │   ├── templates/
│       │   └── postman_collection.json
│       └── application.yml
└── database/
    └── self-contained file/
        └── testmian_quiz_app_main.sql
```

### Key Classes

#### Entities
- `User` - User management with roles
- `Quiz` - Quiz with soft delete support
- `Question` - Question bank
- `QuizAssignment` - Quiz assignments with unique links
- `AuditLog` - Audit logging
- `MagicLink` - Magic link authentication

#### Services
- `QuizService` - Core quiz operations
- `AuditLogService` - Audit logging
- `EmailService` - Email notifications via Kafka
- `MagicLinkService` - Magic link generation/validation
- `QuizRandomizationService` - Question/answer randomization
- `SoftDeleteService` - Soft delete operations

#### Controllers
- `QuizController` - Quiz CRUD operations
- `AuthController` - Authentication endpoints
- `CandidateController` - Candidate-specific operations

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure MySQL is running
   - Check database credentials in `application.yml`
   - Verify database exists

2. **Email Not Sending**
   - Check SMTP credentials
   - Verify Gmail app password if using Gmail
   - Check Kafka connection

3. **Service Discovery Issues**
   - Ensure Eureka server is running first
   - Check service registration in Eureka dashboard

4. **Kafka Connection Issues**
   - Ensure Kafka is running on port 9092
   - Check Kafka logs for errors

### Logs
Check application logs for detailed error information:
```bash
tail -f logs/spring.log
```

## Contributing

1. Follow the existing code structure
2. Add tests for new features
3. Update documentation
4. Ensure no dependency conflicts
5. Test email functionality

## License

This project is licensed under the MIT License.