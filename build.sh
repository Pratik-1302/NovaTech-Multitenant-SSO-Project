#!/usr/bin/env bash
# =============================================================================
# Build Script for Render Deployment
# =============================================================================
# This script is executed during the Render build process
# It ensures a clean build with all dependencies
# =============================================================================

set -e  # Exit on any error

echo "======================================"
echo "NovaTech SSO - Render Build Starting"
echo "======================================"

# Display Java version
echo "Java Version:"
java -version

# Display Maven version
echo ""
echo "Maven Version:"
./mvnw -version

# Clean and package the application
echo ""
echo "Building application..."
./mvnw clean package -DskipTests -Dspring-boot.run.profiles=prod

# Verify JAR was created
if [ -f target/service-app-0.0.1-SNAPSHOT.jar ]; then
    echo ""
    echo "✅ Build successful!"
    echo "JAR file: target/service-app-0.0.1-SNAPSHOT.jar"
    ls -lh target/service-app-0.0.1-SNAPSHOT.jar
else
    echo ""
    echo "❌ Build failed - JAR file not found!"
    exit 1
fi

echo ""
echo "======================================"
echo "Build completed successfully!"
echo "======================================"
