#!/bin/sh
set -e

# Debug: Print original URL (masked for security if needed, but here we need to see the prefix)
# We won't print the full URL to avoid leaking credentials in logs, just the transformation result structure.

echo "Configuring Database URL..."

if [ -z "$DATABASE_URL" ]; then
    echo "WARNING: DATABASE_URL is not set!"
else
    # Transform postgres:// or postgresql:// to jdbc:postgresql://
    # We use | as delimiter to avoid escaping slashes
    export SPRING_DATASOURCE_URL=$(echo "$DATABASE_URL" | sed 's|^postgres.*://|jdbc:postgresql://|')
    
    echo "URL Transformation complete."
    # Check if transformation actually happened
    if echo "$SPRING_DATASOURCE_URL" | grep -q "^jdbc:postgresql://"; then
        echo "SUCCESS: SPRING_DATASOURCE_URL starts with jdbc:postgresql://"
    else
        echo "ERROR: SPRING_DATASOURCE_URL does not start with jdbc:postgresql://"
        echo "Value starts with: $(echo "$SPRING_DATASOURCE_URL" | cut -c1-20)..."
    fi
fi

# Run the application
exec java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar
