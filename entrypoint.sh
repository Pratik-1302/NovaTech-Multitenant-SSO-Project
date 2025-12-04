#!/bin/sh
set -e

echo "Configuring Database URL..."

if [ -z "$DATABASE_URL" ]; then
    echo "WARNING: DATABASE_URL is not set!"
else
    # Parse DATABASE_URL which is in format: postgres://user:pass@host:port/db
    
    # 1. Remove the protocol prefix (postgres:// or postgresql://)
    CLEAN_URL=$(echo "$DATABASE_URL" | sed -e 's/^postgres:\/\///' -e 's/^postgresql:\/\///')

    # 2. Extract User and Password (everything before the @)
    USER_PASS=$(echo "$CLEAN_URL" | cut -d@ -f1)
    
    # 3. Extract Host, Port and DB (everything after the @)
    HOST_DB=$(echo "$CLEAN_URL" | cut -d@ -f2)

    # 4. Split User and Password
    DB_USER=$(echo "$USER_PASS" | cut -d: -f1)
    DB_PASS=$(echo "$USER_PASS" | cut -d: -f2)

    # 5. Construct valid JDBC URL
    JDBC_URL="jdbc:postgresql://${HOST_DB}"
    
    echo "Exporting database configuration..."
    export SPRING_DATASOURCE_URL="$JDBC_URL"
    export SPRING_DATASOURCE_USERNAME="$DB_USER"
    export SPRING_DATASOURCE_PASSWORD="$DB_PASS"
    
    echo "Database connection configured successfully."
fi

# Run the application
exec java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar
