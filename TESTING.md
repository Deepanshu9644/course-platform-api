# API Testing Guide

## Testing with Swagger UI

Swagger UI is the easiest way to test the API. Access it at:
```
http://localhost:8080/swagger-ui.html
```

### Step-by-Step Testing

#### 1. Test Public Endpoints (No Auth Required)

**List All Courses**
1. Expand `GET /api/courses`
2. Click "Try it out"
3. Click "Execute"
4. Verify response shows 2 courses (Physics 101 and Math 101)

**Get Course Details**
1. Expand `GET /api/courses/{courseId}`
2. Click "Try it out"
3. Enter courseId: `physics-101`
4. Click "Execute"
5. Verify response shows topics and subtopics with content

**Search Courses**
1. Expand `GET /api/search`
2. Click "Try it out"
3. Enter query: `velocity`
4. Click "Execute"
5. Verify results show Physics course with matching subtopics

#### 2. Register a New User

1. Expand `POST /api/auth/register`
2. Click "Try it out"
3. Enter request body:
```json
{
  "email": "test@example.com",
  "password": "password123"
}
```
4. Click "Execute"
5. Note the user ID in response

#### 3. Login and Get JWT Token

1. Expand `POST /api/auth/login`
2. Click "Try it out"
3. Enter the same credentials:
```json
{
  "email": "test@example.com",
  "password": "password123"
}
```
4. Click "Execute"
5. **Copy the token from the response** (without quotes)

#### 4. Authorize Swagger with JWT

1. Click the **"Authorize"** button at the top of Swagger UI
2. Enter: `Bearer <your-token>` (replace `<your-token>` with actual token)
   - Example: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
3. Click "Authorize"
4. Click "Close"

Now you can test authenticated endpoints!

#### 5. Enroll in a Course

1. Expand `POST /api/courses/{courseId}/enroll`
2. Click "Try it out"
3. Enter courseId: `physics-101`
4. Click "Execute"
5. **Note the enrollmentId** from the response (you'll need this)

#### 6. Mark a Subtopic as Complete

1. Expand `POST /api/subtopics/{subtopicId}/complete`
2. Click "Try it out"
3. Enter subtopicId: `velocity` (or any other from the course)
4. Click "Execute"
5. Verify response shows completed: true with timestamp

#### 7. View Progress

1. Expand `GET /api/enrollments/{enrollmentId}/progress`
2. Click "Try it out"
3. Enter the enrollmentId from step 5
4. Click "Execute"
5. Verify response shows:
   - Total subtopics: 9
   - Completed subtopics: 1
   - Completion percentage
   - List of completed items

## Testing with cURL

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password123"}'
```

Save the token from response:
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### List Courses
```bash
curl http://localhost:8080/api/courses
```

### Search
```bash
curl "http://localhost:8080/api/search?q=velocity"
```

### Enroll (Authenticated)
```bash
curl -X POST http://localhost:8080/api/courses/physics-101/enroll \
  -H "Authorization: Bearer $TOKEN"
```

### Mark Complete (Authenticated)
```bash
curl -X POST http://localhost:8080/api/subtopics/velocity/complete \
  -H "Authorization: Bearer $TOKEN"
```

### View Progress (Authenticated)
```bash
curl http://localhost:8080/api/enrollments/1/progress \
  -H "Authorization: Bearer $TOKEN"
```

## Testing with Postman

### Setup

1. Import the API:
   - Use OpenAPI spec: `http://localhost:8080/v3/api-docs`
   - Or manually create requests

2. Create Environment Variables:
   - `base_url`: `http://localhost:8080`
   - `token`: (will be set after login)

### Test Collection

#### 1. Register
```
POST {{base_url}}/api/auth/register
Body (JSON):
{
  "email": "test@example.com",
  "password": "password123"
}
```

#### 2. Login
```
POST {{base_url}}/api/auth/login
Body (JSON):
{
  "email": "test@example.com",
  "password": "password123"
}

Test Script (to save token):
pm.environment.set("token", pm.response.json().token);
```

#### 3. List Courses
```
GET {{base_url}}/api/courses
```

#### 4. Get Course
```
GET {{base_url}}/api/courses/physics-101
```

#### 5. Search
```
GET {{base_url}}/api/search?q=velocity
```

#### 6. Enroll
```
POST {{base_url}}/api/courses/physics-101/enroll
Headers:
Authorization: Bearer {{token}}

Test Script (to save enrollmentId):
pm.environment.set("enrollmentId", pm.response.json().enrollmentId);
```

#### 7. Mark Complete
```
POST {{base_url}}/api/subtopics/velocity/complete
Headers:
Authorization: Bearer {{token}}
```

#### 8. View Progress
```
GET {{base_url}}/api/enrollments/{{enrollmentId}}/progress
Headers:
Authorization: Bearer {{token}}
```

## Test Scenarios

### Happy Path
1. ✅ Register user
2. ✅ Login and get token
3. ✅ Browse courses (no auth)
4. ✅ Search for content (no auth)
5. ✅ Enroll in course
6. ✅ Mark subtopics complete
7. ✅ View progress

### Error Cases

#### Duplicate Registration
```bash
# Register same email twice
# Expected: 409 Conflict
```

#### Invalid Login
```bash
# Wrong password
# Expected: 401 Unauthorized
```

#### Enroll Without Auth
```bash
# Don't send token
# Expected: 401 Unauthorized
```

#### Duplicate Enrollment
```bash
# Enroll in same course twice
# Expected: 409 Conflict
```

#### Mark Complete Without Enrollment
```bash
# Complete subtopic without enrolling first
# Expected: 403 Forbidden
```

#### Invalid Course ID
```bash
# Use non-existent course ID
# Expected: 404 Not Found
```

## Search Test Cases

Test various queries:

| Query | Expected Results |
|-------|------------------|
| `velocity` | Physics course (subtopic + content match) |
| `Newton` | Physics course (dynamics topic) |
| `rate of change` | Math course (functions topic) |
| `algebra` | Math course (topic title) |
| `energy` | Physics course (topic title + content) |
| `triangle` | Math course (geometry) |
| `VELOCITY` | Same as `velocity` (case insensitive) |
| `velo` | Should match `velocity` (partial match) |

## Performance Testing

### Simple Load Test with cURL

```bash
# Test 100 requests
for i in {1..100}; do
  curl -s http://localhost:8080/api/courses > /dev/null &
done
wait
```

### Using Apache Bench

```bash
# Install ab (Apache Bench)
# macOS: included
# Ubuntu: apt-get install apache2-utils

# Test with 100 requests, 10 concurrent
ab -n 100 -c 10 http://localhost:8080/api/courses
```

## Validation Testing

Test request validation:

### Missing Email
```json
{
  "password": "password123"
}
// Expected: 400 Bad Request
```

### Invalid Email Format
```json
{
  "email": "notanemail",
  "password": "password123"
}
// Expected: 400 Bad Request
```

### Short Password
```json
{
  "email": "test@example.com",
  "password": "12345"
}
// Expected: 400 Bad Request
```

## Integration Testing

Test the complete flow:

```bash
#!/bin/bash

# 1. Register
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"integration@test.com","password":"test1234"}')

echo "Register: $REGISTER_RESPONSE"

# 2. Login
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"integration@test.com","password":"test1234"}')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "Token obtained"

# 3. Enroll
ENROLL_RESPONSE=$(curl -s -X POST http://localhost:8080/api/courses/physics-101/enroll \
  -H "Authorization: Bearer $TOKEN")

ENROLLMENT_ID=$(echo $ENROLL_RESPONSE | jq -r '.enrollmentId')
echo "Enrolled: $ENROLLMENT_ID"

# 4. Complete subtopics
for subtopic in "speed" "velocity" "acceleration"; do
  curl -s -X POST http://localhost:8080/api/subtopics/$subtopic/complete \
    -H "Authorization: Bearer $TOKEN" > /dev/null
  echo "Completed: $subtopic"
done

# 5. Check progress
PROGRESS=$(curl -s http://localhost:8080/api/enrollments/$ENROLLMENT_ID/progress \
  -H "Authorization: Bearer $TOKEN")

echo "Progress: $PROGRESS"
```

## Expected Response Times

On local development:
- List courses: < 100ms
- Search: < 200ms
- Enroll: < 150ms
- Mark complete: < 100ms

On deployment (depends on hosting):
- Add ~100-300ms for network latency
- Database queries should still be < 100ms
