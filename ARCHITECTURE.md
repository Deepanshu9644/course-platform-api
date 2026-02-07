# Course Platform API - Architecture

## System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  Swagger UI  │  │   Web App    │  │  Mobile App  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP/REST + JWT
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Spring Boot Application                     │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Controller Layer                       │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │   │
│  │  │   Auth   │ │  Course  │ │  Search  │ │ Progress │   │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │               Security Filter Chain                      │   │
│  │  ┌────────────────────────────────────────────────┐     │   │
│  │  │   JWT Authentication Filter                     │     │   │
│  │  │   - Extracts & validates JWT                    │     │   │
│  │  │   - Sets security context                       │     │   │
│  │  └────────────────────────────────────────────────┘     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    Service Layer                         │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐   │   │
│  │  │   Auth   │ │  Course  │ │Enrollment│ │ Progress │   │   │
│  │  │ Service  │ │ Service  │ │ Service  │ │ Service  │   │   │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘   │   │
│  └─────────────────────────────────────────────────────────┘   │
│                              │                                    │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                  Repository Layer (JPA)                  │   │
│  │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐          │   │
│  │  │ User │ │Course│ │Topic │ │Subtop│ │Enroll│ ...      │   │
│  │  │ Repo │ │ Repo │ │ Repo │ │ Repo │ │ Repo │          │   │
│  │  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘          │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              │ JDBC
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       PostgreSQL Database                        │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │  Tables: users, courses, topics, subtopics,              │  │
│  │          enrollments, subtopic_progress                   │  │
│  └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow

### Public Endpoints (e.g., Search)
```
1. Client → Controller (no auth check)
2. Controller → Service
3. Service → Repository
4. Repository → Database (JPQL query)
5. Database → Repository (results)
6. Repository → Service (entities)
7. Service → Controller (DTOs)
8. Controller → Client (JSON)
```

### Authenticated Endpoints (e.g., Enroll)
```
1. Client → JWT Filter (validates token)
2. JWT Filter → Security Context (set auth)
3. Security Context → Controller (authorized)
4. Controller → Service (with user context)
5. Service → Repository
6. Repository → Database
7. Database → Repository
8. Repository → Service
9. Service → Controller
10. Controller → Client
```

## Database Schema

```
┌──────────────┐
│    users     │
├──────────────┤
│ id (PK)      │
│ email (UQ)   │
│ password     │
│ created_at   │
└──────────────┘
       │
       │ 1:N
       ▼
┌──────────────┐       ┌──────────────┐
│ enrollments  │  N:1  │   courses    │
├──────────────┤───────├──────────────┤
│ id (PK)      │       │ id (PK)      │
│ user_id (FK) │       │ title        │
│ course_id(FK)│       │ description  │
│ enrolled_at  │       └──────────────┘
└──────────────┘              │
                              │ 1:N
                              ▼
                       ┌──────────────┐
                       │    topics    │
                       ├──────────────┤
                       │ id (PK)      │
                       │ course_id(FK)│
                       │ title        │
                       └──────────────┘
                              │
                              │ 1:N
                              ▼
                       ┌──────────────┐
                       │  subtopics   │
                       ├──────────────┤
                       │ id (PK)      │
                       │ topic_id (FK)│
                       │ title        │
                       │ content      │
                       └──────────────┘
                              │
       ┌──────────────────────┘
       │ N:1
       ▼
┌─────────────────────┐
│ subtopic_progress   │
├─────────────────────┤
│ id (PK)             │
│ user_id (FK)        │
│ subtopic_id (FK)    │
│ completed           │
│ completed_at        │
└─────────────────────┘
```

## Security Flow

```
┌─────────────┐
│   Register  │
└─────────────┘
       │
       ▼
┌─────────────────────┐
│  Hash Password      │
│  (BCrypt)           │
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│  Save to Database   │
└─────────────────────┘

┌─────────────┐
│    Login    │
└─────────────┘
       │
       ▼
┌─────────────────────┐
│ Verify Password     │
│ (BCrypt.matches)    │
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│ Generate JWT        │
│ (HS256, 24h exp)    │
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│ Return Token        │
└─────────────────────┘

┌─────────────────────┐
│  Protected Request  │
│  (with JWT header)  │
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│ JWT Filter          │
│ - Extract token     │
│ - Validate          │
│ - Set context       │
└─────────────────────┘
       │
       ▼
┌─────────────────────┐
│ Process Request     │
└─────────────────────┘
```

## Request/Response Flow Examples

### Example 1: Search Request
```
GET /api/search?q=velocity

┌──────────┐    ┌────────────┐    ┌───────────┐    ┌──────────┐
│  Client  │───▶│ Controller │───▶│  Service  │───▶│   Repo   │
└──────────┘    └────────────┘    └───────────┘    └──────────┘
                                                           │
                                                           ▼
                                                    ┌──────────┐
                                                    │ Database │
                                                    └──────────┘
                                                           │
┌──────────┐    ┌────────────┐    ┌───────────┐    ┌──────────┐
│  Client  │◀───│ Controller │◀───│  Service  │◀───│   Repo   │
└──────────┘    └────────────┘    └───────────┘    └──────────┘
   (JSON)         (DTO)            (Entity)         (Entity)
```

### Example 2: Enroll Request (Authenticated)
```
POST /api/courses/physics-101/enroll
Authorization: Bearer <token>

┌──────────┐    ┌───────────┐    ┌────────────┐
│  Client  │───▶│JWT Filter │───▶│ Controller │
└──────────┘    └───────────┘    └────────────┘
                     │                   │
                     ▼                   ▼
              ┌────────────┐      ┌───────────┐
              │  Security  │      │  Service  │
              │  Context   │      └───────────┘
              └────────────┘            │
                                        ▼
                                  ┌──────────┐
                                  │Check user│
                                  │enrolled? │
                                  └──────────┘
                                        │
                                        ▼
                                  ┌──────────┐
                                  │  Save    │
                                  │Enrollment│
                                  └──────────┘
                                        │
┌──────────┐    ┌────────────┐    ┌───────────┐
│  Client  │◀───│ Controller │◀───│  Service  │
└──────────┘    └────────────┘    └───────────┘
   (JSON)         (DTO)             (Entity)
```

## Component Responsibilities

### Controller Layer
- Handle HTTP requests/responses
- Validate request parameters
- Call appropriate services
- Return DTOs (never entities)
- Handle HTTP status codes

### Service Layer
- Business logic
- Transaction management
- Coordinate multiple repositories
- Convert entities to DTOs
- Enforce business rules

### Repository Layer
- Database operations
- JPQL queries
- Entity management
- No business logic

### Security Layer
- JWT generation and validation
- Password encryption
- Authorization checks
- Security context management

### Entity Layer
- Database table mapping
- Relationships
- Constraints
- Lifecycle callbacks
