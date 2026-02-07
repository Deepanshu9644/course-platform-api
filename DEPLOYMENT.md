# Deployment Guide

## Quick Deployment Options

### Option 1: Railway (Recommended - Easiest)

1. **Create Railway Account**
   - Go to [railway.app](https://railway.app)
   - Sign up with GitHub

2. **Deploy from GitHub**
   - Create a new project
   - Select "Deploy from GitHub repo"
   - Connect your repository
   - Railway will auto-detect Java/Maven

3. **Add PostgreSQL**
   - Click "New" → "Database" → "Add PostgreSQL"
   - Railway automatically creates and links the database

4. **Configure Environment Variables**
   Railway auto-configures `DATABASE_URL`, but you may want to set:
   ```
   JWT_SECRET=your-secret-key-here
   ```

5. **Deploy**
   - Railway automatically deploys on push
   - Access via: `https://your-app.up.railway.app`
   - Swagger UI: `https://your-app.up.railway.app/swagger-ui.html`

### Option 2: Render

1. **Create Render Account**
   - Go to [render.com](https://render.com)
   - Sign up with GitHub

2. **Create PostgreSQL Database**
   - Click "New" → "PostgreSQL"
   - Note the Internal Database URL

3. **Create Web Service**
   - Click "New" → "Web Service"
   - Connect your GitHub repository
   - Settings:
     - **Name**: course-platform-api
     - **Environment**: Java
     - **Build Command**: `mvn clean install`
     - **Start Command**: `java -jar target/course-platform-api-1.0.0.jar`

4. **Environment Variables**
   ```
   DATABASE_URL=<your-internal-database-url>
   DATABASE_USERNAME=<from-database-credentials>
   DATABASE_PASSWORD=<from-database-credentials>
   JWT_SECRET=your-secret-key-here
   ```

5. **Deploy**
   - Render builds and deploys automatically
   - Access Swagger: `https://your-app.onrender.com/swagger-ui.html`

### Option 3: Fly.io

1. **Install Fly CLI**
   ```bash
   curl -L https://fly.io/install.sh | sh
   ```

2. **Login and Initialize**
   ```bash
   fly auth login
   fly launch
   ```

3. **Create PostgreSQL**
   ```bash
   fly postgres create
   fly postgres attach <postgres-app-name>
   ```

4. **Deploy**
   ```bash
   fly deploy
   ```

### Option 4: Heroku

1. **Install Heroku CLI**
   ```bash
   curl https://cli-assets.heroku.com/install.sh | sh
   ```

2. **Login and Create App**
   ```bash
   heroku login
   heroku create course-platform-api
   ```

3. **Add PostgreSQL**
   ```bash
   heroku addons:create heroku-postgresql:essential-0
   ```

4. **Configure Buildpack**
   ```bash
   heroku buildpacks:set heroku/java
   ```

5. **Set Environment Variables**
   ```bash
   heroku config:set JWT_SECRET=your-secret-key
   ```

6. **Deploy**
   ```bash
   git push heroku main
   ```

7. **Access**
   ```bash
   heroku open
   # Navigate to /swagger-ui.html
   ```

## Docker Deployment

### Using Docker Compose (Local)

```bash
# Build and run
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

### Using Docker (Manual)

```bash
# Build image
docker build -t course-platform-api .

# Run PostgreSQL
docker run -d \
  --name coursedb \
  -e POSTGRES_DB=coursedb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

# Run application
docker run -d \
  --name course-api \
  --link coursedb:postgres \
  -e DATABASE_URL=jdbc:postgresql://coursedb:5432/coursedb \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  -p 8080:8080 \
  course-platform-api
```

## Environment Variables Reference

| Variable | Description | Required | Default |
|----------|-------------|----------|---------|
| `DATABASE_URL` | PostgreSQL JDBC URL | Yes | jdbc:postgresql://localhost:5432/coursedb |
| `DATABASE_USERNAME` | Database username | Yes | postgres |
| `DATABASE_PASSWORD` | Database password | Yes | postgres |
| `JWT_SECRET` | Secret key for JWT signing | No | (has default) |
| `PORT` | Application port | No | 8080 |

## Verification Checklist

After deployment, verify:

- [ ] Application is accessible at public URL
- [ ] Swagger UI loads: `/swagger-ui.html`
- [ ] Can view all courses: `GET /api/courses`
- [ ] Search works: `GET /api/search?q=velocity`
- [ ] Can register: `POST /api/auth/register`
- [ ] Can login: `POST /api/auth/login`
- [ ] JWT authorization works in Swagger
- [ ] Can enroll in course
- [ ] Can mark subtopic complete
- [ ] Can view progress

## Troubleshooting

### Database Connection Issues

```bash
# Check DATABASE_URL format
jdbc:postgresql://HOST:PORT/DATABASE

# Verify credentials
psql -h HOST -U USERNAME -d DATABASE
```

### Application Won't Start

```bash
# Check logs
# Railway: View in dashboard
# Render: View in dashboard
# Heroku: heroku logs --tail
# Docker: docker logs course-api
```

### Port Issues

Most platforms automatically assign ports via `PORT` environment variable. Spring Boot reads this automatically.

### Memory Issues

If app crashes due to memory:
```bash
# Add to start command
java -Xmx512m -jar app.jar
```

## Performance Optimization

For production:

1. **Enable Connection Pooling**
   ```properties
   spring.datasource.hikari.maximum-pool-size=10
   ```

2. **Add Caching**
   ```properties
   spring.cache.type=simple
   ```

3. **Tune JPA**
   ```properties
   spring.jpa.show-sql=false
   spring.jpa.properties.hibernate.generate_statistics=false
   ```

## Security Recommendations

1. Use strong JWT secret (generate random key)
2. Enable HTTPS (most platforms do this automatically)
3. Set secure CORS origins in production
4. Rotate JWT secret regularly
5. Use database connection encryption

## Monitoring

Add health check endpoint:
```java
@GetMapping("/health")
public String health() {
    return "OK";
}
```

Most platforms automatically use Spring Boot Actuator:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

## Cost Optimization

- Railway: Free tier includes $5/month credits
- Render: Free tier available with limitations
- Fly.io: Free tier includes 3GB storage
- Heroku: Hobby tier recommended ($7/month)

Choose based on:
- Expected traffic
- Database size requirements
- Budget constraints
