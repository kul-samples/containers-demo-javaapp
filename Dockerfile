# Use the official Maven image to build the project
FROM maven:3.8.6-eclipse-temurin-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project files
COPY pom.xml ./
COPY src ./src

# Package the application as a WAR file
RUN mvn clean package

# Use the official Tomcat image
FROM tomcat:10.1.31-jdk11

# Install PostgreSQL client in the Tomcat image
RUN apt-get update && apt-get install -y postgresql-client && rm -rf /var/lib/apt/lists/*

# Set the working directory for Tomcat
WORKDIR /usr/local/tomcat

# Copy the built WAR file from the build stage to the Tomcat webapps directory
COPY --from=build /app/target/kmayer-1.0.war /usr/local/tomcat/webapps/kmayer.war

# Expose the default port for Tomcat
EXPOSE 8080

# Set environment variables for PostgreSQL connection
ENV PGHOST=postgres-container
ENV PGDATABASE=postgres
ENV PGPORT=5432
ENV PGUSER=postgres
ENV PGPASSWORD=root

# Start the Tomcat server
CMD ["catalina.sh", "run"]
