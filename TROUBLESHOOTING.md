# Troubleshooting Guide

## Common Issues and Solutions

### Build Issues

#### Maven Build Fails - Java Version

**Symptom:**
```
[ERROR] Failed to execute goal ... requires Java 17 or later
```

**Solution:**
```bash
# Check your Java version
java -version

# Should show Java 17 or higher
# If not, install Java 17+

# Ubuntu/Debian
sudo apt install openjdk-17-jdk

# macOS
brew install openjdk@17

# Set JAVA_HOME
export JAVA_HOME=/path/to/java17
```

#### Maven Dependencies Not Downloading

**Symptom:**
```
[ERROR] Failed to execute goal ... Could not resolve dependencies
```

**Solution:**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Try again
mvn clean install
```

### Database Issues

#### Cannot Connect to Database

**Symptom:**
```
org.postgresql.util.PSQLException: Connection refused
```

**Solution:**
1. Check if PostgreSQL is running:
   ```bash
   # Ubuntu
   sudo systemctl status postgresql
   
   # macOS
   brew services list | grep postgresql
   ```

2. Start PostgreSQL if needed:
   ```bash
   # Ubuntu
   sudo systemctl start postgresql
   
   # macOS
   brew services start postgresql
   ```

3. Verify connection details in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/coursedb
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

#### Database Does Not Exist

**Symptom:**
```
org.postgresql.util.PSQLException: FATAL: database "coursedb" does not exist
```

**Solution:**
```bash
# Create the database
createdb coursedb

# Or using psql
psql -U postgres
CREATE DATABASE coursedb;
\q
```

#### Authentication Failed for Database

**Symptom:**
```
org.postgresql.util.PSQLException: FATAL: password authentication failed
```

**Solution:**
1. Check username and password in `application.properties`
2. Reset PostgreSQL password if needed:
   ```bash
   sudo -u postgres psql
   ALTER USER postgres PASSWORD 'newpassword';
   \q
   ```
3. Update `application.properties` with new password

### Application Startup Issues

#### Port Already in Use

**Symptom:**
```
Web server failed to start. Port 8080 was already in use.
```

**Solution:**
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or use a different port in application.properties
server.port=8081
```

#### Seed Data Fails to Load

**Symptom:**
```
Error loading seed data: ...
```

**Solution:**
1. Check if `seed-data.json` exists in `src/main/resources/`
2. Verify JSON is valid:
   ```bash
   python -m json.tool src/main/resources/seed-data.json
   ```
3. Check database connection
4. Clear database and restart:
   ```sql
   DROP DATABASE coursedb;
   CREATE DATABASE coursedb;
   ```

### JWT/Authentication Issues

#### JWT Token Invalid or Expired

**Symptom:**
```json
{
  "error": "Unauthorized",
  "message": "JWT token is missing or invalid"
}
```

**Solution:**
1. Get a new token by logging in again
2. Make sure token is in header: `Authorization: Bearer <token>`
3. Check token hasn't expired (24 hours default)

#### Cannot Authorize in Swagger

**Problem:**
Token doesn't work in Swagger after clicking "Authorize"

**Solution:**
1. Make sure to include "Bearer " prefix:
   - ✅ Correct: `Bearer eyJhbGc...`
   - ❌ Wrong: `eyJhbGc...`
2. Copy token without quotes
3. Click "Authorize" then "Close"
4. Try the endpoint again

### API Issues

#### 401 Unauthorized on Protected Endpoints

**Solution:**
1. Verify you're logged in and have a valid token
2. Check Authorization header format:
   ```
   Authorization: Bearer <your-token>
   ```
3. Ensure token hasn't expired
4. Re-login if needed

#### 403 Forbidden - Cannot Mark Subtopic Complete

**Symptom:**
```json
{
  "error": "Forbidden",
  "message": "You must be enrolled in this course..."
}
```

**Solution:**
1. Enroll in the course first:
   ```
   POST /api/courses/{courseId}/enroll
   ```
2. Then mark subtopics complete

#### 404 Not Found - Course or Subtopic

**Solution:**
1. Check the ID is correct:
   - Course IDs: `physics-101`, `math-101`
   - Subtopic IDs: `velocity`, `speed`, `acceleration`, etc.
2. List all courses to see available IDs:
   ```
   GET /api/courses
   ```

#### 409 Conflict - Already Enrolled

**Symptom:**
```json
{
  "error": "Already enrolled",
  "message": "You are already enrolled in this course"
}
```

**This is expected behavior!** You can only enroll once per course.

### Search Issues

#### Search Returns No Results

**Problem:**
Searching for a term that should exist returns empty results

**Solution:**
1. Check spelling of search term
2. Try partial matches: `velo` instead of `velocity`
3. Search is case-insensitive, so `VELOCITY` = `velocity`
4. Verify seed data loaded:
   ```
   GET /api/courses
   ```
5. Check database has data:
   ```sql
   SELECT COUNT(*) FROM courses;
   SELECT COUNT(*) FROM subtopics;
   ```

#### Search is Slow

**Solution:**
1. Add database indexes:
   ```sql
   CREATE INDEX idx_course_title ON courses(title);
   CREATE INDEX idx_subtopic_content ON subtopics USING gin(to_tsvector('english', content));
   ```
2. Consider implementing Elasticsearch for better performance

### Deployment Issues

#### Railway: Build Fails

**Solution:**
1. Check build logs in Railway dashboard
2. Ensure `pom.xml` is in repository root
3. Verify Java version in `pom.xml` matches Railway's
4. Check all dependencies are available

#### Render: Database Connection Timeout

**Solution:**
1. Use Internal Database URL (not External)
2. Format: `jdbc:postgresql://dpg-xxx.render.com/dbname`
3. Check database is in same region
4. Verify security group settings

#### Heroku: Application Error (H10)

**Solution:**
1. Check logs: `heroku logs --tail`
2. Ensure `PORT` environment variable is used
3. Verify PostgreSQL addon is attached
4. Check buildpack is set to Java

#### Application Crashes After Deploy

**Solution:**
1. Check environment variables are set correctly
2. Verify database URL format
3. Check memory limits
4. View logs for specific error messages

### Docker Issues

#### Docker Build Fails

**Symptom:**
```
ERROR: failed to solve: failed to compute cache key
```

**Solution:**
```bash
# Clear Docker cache
docker system prune -a

# Rebuild
docker-compose build --no-cache
```

#### Container Can't Connect to Database

**Solution:**
1. Make sure using container network:
   ```yaml
   DATABASE_URL: jdbc:postgresql://postgres:5432/coursedb
   ```
   Note: Use service name `postgres`, not `localhost`

2. Check containers are on same network:
   ```bash
   docker-compose ps
   docker network inspect course-platform-api_default
   ```

### Performance Issues

#### API Responses are Slow

**Solution:**
1. Enable query logging to identify slow queries:
   ```properties
   spring.jpa.show-sql=true
   logging.level.org.hibernate.SQL=DEBUG
   ```

2. Add database indexes:
   ```sql
   CREATE INDEX idx_enrollment_user_course ON enrollments(user_id, course_id);
   CREATE INDEX idx_progress_user_subtopic ON subtopic_progress(user_id, subtopic_id);
   ```

3. Enable JPA query cache:
   ```properties
   spring.jpa.properties.hibernate.cache.use_second_level_cache=true
   ```

#### Database Queries are Slow

**Solution:**
1. Check explain plans:
   ```sql
   EXPLAIN ANALYZE SELECT * FROM courses WHERE title LIKE '%physics%';
   ```

2. Add missing indexes
3. Consider pagination for large result sets
4. Use database connection pooling (HikariCP is default)

### Data Issues

#### Seed Data Not Loading

**Check:**
1. Database has no existing data (seed only loads if empty)
2. JSON file is valid
3. Application has write permissions
4. Check logs for specific errors

**Force Reload:**
```sql
-- Clear all data
TRUNCATE TABLE subtopic_progress, enrollments, subtopics, topics, courses, users CASCADE;
```

Restart application - seed data will load.

#### Data Inconsistency

**Solution:**
1. Verify foreign key constraints:
   ```sql
   SELECT * FROM information_schema.table_constraints 
   WHERE constraint_type = 'FOREIGN KEY';
   ```

2. Check for orphaned records:
   ```sql
   -- Enrollments without users
   SELECT * FROM enrollments e 
   LEFT JOIN users u ON e.user_id = u.id 
   WHERE u.id IS NULL;
   ```

### Testing Issues

#### Cannot Test in Swagger After Deployment

**Solution:**
1. Ensure Swagger UI is accessible:
   ```
   https://your-app.com/swagger-ui.html
   ```

2. Check CORS settings allow your domain

3. Verify public endpoints work without auth

4. Test authentication flow:
   - Register → Login → Authorize → Test

#### Postman/cURL Requests Fail

**Common mistakes:**
1. Missing `Content-Type: application/json` header
2. Wrong Authorization header format
3. Using wrong URL or endpoint
4. Token expired (login again)

**Test with cURL:**
```bash
# Test public endpoint
curl -v http://localhost:8080/api/courses

# Test with auth
TOKEN="your-token-here"
curl -v http://localhost:8080/api/courses/physics-101/enroll \
  -H "Authorization: Bearer $TOKEN" \
  -X POST
```

## Getting Help

### Debug Mode

Enable debug logging:
```properties
logging.level.root=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate=DEBUG
```

### Check Application Health

```bash
# If actuator is enabled
curl http://localhost:8080/actuator/health

# Check if application is responding
curl http://localhost:8080/api/courses
```

### View Application Logs

```bash
# Spring Boot application
# Logs appear in console

# Docker
docker-compose logs -f app

# Heroku
heroku logs --tail

# Railway/Render
# View in dashboard
```

### Database Inspection

```bash
# Connect to database
psql -h localhost -U postgres -d coursedb

# Useful queries
\dt                          -- List tables
SELECT * FROM courses;       -- View courses
SELECT * FROM users;         -- View users
SELECT * FROM enrollments;   -- View enrollments
```

## Prevention Tips

### Before Deployment

- ✅ Test locally first
- ✅ Verify all endpoints work in Swagger
- ✅ Check environment variables are set
- ✅ Test with fresh database
- ✅ Verify seed data loads
- ✅ Test authentication flow
- ✅ Check error responses

### Best Practices

1. **Use environment variables** for all configuration
2. **Never commit passwords** or secrets
3. **Test API endpoints** after each change
4. **Keep logs** at INFO level in production
5. **Monitor** application health
6. **Backup** database regularly

## Still Having Issues?

1. **Check the logs** - most errors show there
2. **Search error messages** - often someone else had same issue
3. **Verify configuration** - double-check all settings
4. **Test incrementally** - isolate the problem
5. **Start fresh** - sometimes rebuilding fixes issues

## Quick Reset

If everything is broken, start fresh:

```bash
# Stop application
# Kill running processes on port 8080

# Clear database
dropdb coursedb
createdb coursedb

# Clean build
mvn clean install

# Restart
mvn spring-boot:run
```
