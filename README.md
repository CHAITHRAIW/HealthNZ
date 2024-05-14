This Github repository contains a Springboot Microservice with a Postgres Database that can be dockerized:

HealthNZ

Summary
Setup and Pre-requisites
Running the Microservice
Verify the containers are running
Microservice
Both databases
Cleaning up Exited Containers
Populating the Microservice Database
Testing the Microservice

Setup and Pre-requisites
# HealthNZ

This repository contains a Java application that connects to a PostgreSQL database and lists all table names within the `healthNZRules_schema` schema in the `healthnzrules` database.

## Prerequisites

Before you begin, ensure you have the following installed:
- Java JDK 8 or higher
- Docker
- Maven (if managing dependencies through Maven)

## Setup

### PostgreSQL Setup with Docker

1. **Pull the PostgreSQL Image**
   ```bash
   docker pull postgres

2.**Start PostgreSQL Container**
docker run --name my-postgres-db -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres

3.**Access PostgreSQL Shell**
docker exec -it my-postgres-db psql -U postgres

4.**Create Database and Schema**
CREATE DATABASE healthnzrules;
\c healthnzrules
CREATE SCHEMA healthNZRules_schema;

5**Creating table DroolFiles**
CREATE TABLE healthnzrules_schema.DroolFiles (
    file_id SERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL
);

6.**Creating table DroolRules**
CREATE TABLE healthnzrules_schema.DroolRules (
    rule_id SERIAL PRIMARY KEY,
    rule_name VARCHAR(255) NOT NULL,
    rule_content TEXT NOT NULL
);

7.**Verify Table Creation**
SELECT table_name FROM information_schema.tables WHERE table_schema = 'healthnzrules_schema';






