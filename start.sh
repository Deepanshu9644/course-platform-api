#!/bin/bash

echo "=========================================="
echo "Course Platform API - Quick Start"
echo "=========================================="
echo ""

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version is $JAVA_VERSION. Please install Java 17 or higher."
    exit 1
fi

echo "✅ Java $JAVA_VERSION detected"

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6 or higher."
    exit 1
fi

echo "✅ Maven detected"

# Check if PostgreSQL is running
if ! command -v psql &> /dev/null; then
    echo "⚠️  PostgreSQL client not found. Make sure PostgreSQL is installed and running."
    echo "   Continue? (y/n)"
    read -r response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo ""
echo "Setting up the database..."
echo "----------------------------------------"
echo "Database configuration:"
echo "  Host: localhost"
echo "  Port: 5432"
echo "  Database: coursedb"
echo "  Username: postgres"
echo ""
echo "Make sure PostgreSQL is running and the database 'coursedb' exists."
echo "To create it, run: createdb coursedb"
echo ""
echo "Continue with these settings? (y/n)"
read -r response

if [[ ! "$response" =~ ^[Yy]$ ]]; then
    echo "Please update src/main/resources/application.properties with your database settings."
    exit 1
fi

echo ""
echo "Building the application..."
echo "----------------------------------------"
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi

echo ""
echo "✅ Build successful!"
echo ""
echo "Starting the application..."
echo "----------------------------------------"
echo ""

mvn spring-boot:run &
APP_PID=$!

echo "Application is starting (PID: $APP_PID)..."
echo "Waiting for application to be ready..."

# Wait for application to start
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 || \
       curl -s http://localhost:8080/api/courses > /dev/null 2>&1; then
        echo ""
        echo "=========================================="
        echo "✅ Application is ready!"
        echo "=========================================="
        echo ""
        echo "📚 Swagger UI: http://localhost:8080/swagger-ui.html"
        echo "📋 API Docs: http://localhost:8080/v3/api-docs"
        echo "🔍 Test search: http://localhost:8080/api/search?q=velocity"
        echo ""
        echo "Press Ctrl+C to stop the application"
        echo "=========================================="
        wait $APP_PID
        exit 0
    fi
    sleep 2
done

echo "❌ Application failed to start within 60 seconds"
echo "Check the logs above for errors"
kill $APP_PID 2>/dev/null
exit 1
