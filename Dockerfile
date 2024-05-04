# Use the official PostgreSQL image from Docker Hub
FROM postgres:latest

# Set environment variables
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres
ENV POSTGRES_DB=sample_management

# Expose the PostgreSQL port
EXPOSE 5432
