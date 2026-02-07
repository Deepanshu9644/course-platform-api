# Course Platform API

A backend service for a learning platform where users can browse courses, enroll, and track their learning progress.

## Features

- **Public Course Browsing**: View all courses and search content without authentication
- **User Authentication**: JWT-based registration and login
- **Course Enrollment**: Authenticated users can enroll in courses
- **Progress Tracking**: Mark subtopics as completed and view progress
- **Search Functionality**: Full-text search across courses, topics, and content
- **API Documentation**: Interactive Swagger UI for testing

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **PostgreSQL** - Database
- **Spring Data JPA** - Data access
- **Spring Security** - JWT authentication
- **Springdoc OpenAPI** - API documentation
- **Lombok** - Reduce boilerplate
- **Maven** - Build tool

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

## Local Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd course-platform-api
```

### 2. Configure Database

Create a PostgreSQL database:

```sql
CREATE DATABASE coursedb;
```

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/coursedb
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Access Swagger UI

Open your browser and navigate to:

```
http://localhost:8080/swagger-ui.html
```

## Deployment

### Environment Variables

For deployment, set these environment variables:

- `DATABASE_URL` - PostgreSQL connection URL
- `DATABASE_USERNAME` - Database username
- `DATABASE_PASSWORD` - Database password
- `JWT_SECRET` - Secret key for JWT signing (optional, has default)
- `PORT` - Application port (default: 8080)

### Example Deployment (Railway)

1. Create a new project on Railway
2. Add PostgreSQL database
3. Deploy from GitHub repository
4. Set environment variables
5. Access via the provided Railway URL

### Example Deployment (Render)

1. Create a new Web Service
2. Connect your GitHub repository
3. Select "Java" environment
4. Add PostgreSQL database
5. Set environment variables
6. Deploy

## API Documentation

### Authentication Endpoints

#### Register
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "student@example.com",
  "password": "securePassword123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "student@example.com",
  "password": "securePassword123"
}
```

### Public Endpoints (No Auth Required)

#### List All Courses
```http
GET /api/courses
```

#### Get Course by ID
```http
GET /api/courses/{courseId}
```

#### Search Courses
```http
GET /api/search?q=velocity
```

### Authenticated Endpoints (JWT Required)

Add the JWT token to the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

#### Enroll in Course
```http
POST /api/courses/{courseId}/enroll
Authorization: Bearer <token>
```

#### Mark Subtopic Complete
```http
POST /api/subtopics/{subtopicId}/complete
Authorization: Bearer <token>
```

#### View Enrollment Progress
```http
GET /api/enrollments/{enrollmentId}/progress
Authorization: Bearer <token>
```

## Testing with Swagger

1. Navigate to Swagger UI: `http://localhost:8080/swagger-ui.html`
2. Test public endpoints immediately (no auth needed)
3. For authenticated endpoints:
   - Register a new user using `/api/auth/register`
   - Login using `/api/auth/login` and copy the token
   - Click "Authorize" button at the top
   - Enter: `Bearer <your-token>`
   - Now you can test authenticated endpoints

## Database Schema

### Tables

- **users** - User accounts
- **courses** - Course information
- **topics** - Topics within courses
- **subtopics** - Subtopics with markdown content
- **enrollments** - User course enrollments
- **subtopic_progress** - Tracks completed subtopics

### Relationships

- Course → Topics (One-to-Many)
- Topic → Subtopics (One-to-Many)
- User → Enrollments (One-to-Many)
- Course → Enrollments (One-to-Many)
- User → SubtopicProgress (One-to-Many)
- Subtopic → SubtopicProgress (One-to-Many)

## Seed Data

The application automatically loads seed data on first startup:

- **Physics 101** - Introduction to Physics (3 topics, 9 subtopics)
- **Math 101** - Basic Mathematics (3 topics, 9 subtopics)

Seed data includes:
- Course titles and descriptions
- Topics with subtopics
- Markdown-formatted content for each subtopic

## Search Implementation

The search functionality uses PostgreSQL's `LIKE` operator for:
- Case-insensitive matching
- Partial matches
- Searching across:
  - Course titles and descriptions
  - Topic titles
  - Subtopic titles and content

Results include snippets showing where the match was found.

## Error Handling

The API returns consistent error responses:

```json
{
  "error": "Error Type",
  "message": "Human-readable description",
  "timestamp": "2025-12-21T10:30:00Z"
}
```

HTTP Status Codes:
- `200 OK` - Successful GET
- `201 Created` - Successful POST
- `400 Bad Request` - Invalid input
- `401 Unauthorized` - Missing/invalid token
- `403 Forbidden` - Valid token but insufficient permissions
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate enrollment/email

## Security

- Passwords are encrypted using BCrypt
- JWT tokens expire after 24 hours (configurable)
- Stateless authentication (no server-side sessions)
- CORS enabled for all origins
- Course content is read-only (no CRUD operations)

## Project Structure

```
src/main/java/com/learning/courseplatform/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── dto/              # Data Transfer Objects
├── entity/           # JPA entities
├── exception/        # Custom exceptions
├── repository/       # JPA repositories
├── security/         # Security configuration
└── service/          # Business logic

src/main/resources/
├── application.properties
└── seed-data.json
```

## Future Enhancements

Potential improvements for bonus points:

- **Elasticsearch Integration** - Better search with ranking
- **Semantic Search** - Using embeddings for relevance
- **Fuzzy Matching** - Tolerate spelling mistakes
- **Pagination** - For large result sets
- **Caching** - Redis for frequently accessed data

## Support

For issues or questions, please create an issue in the GitHub repository.

## License

This project is created as an assignment for evaluation purposes.
