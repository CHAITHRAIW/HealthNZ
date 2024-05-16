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

# HealthNZ
## Summary

This repository contains a Java application that connects to a PostgreSQL database and lists all table names within the `healthNZRules_schema` schema in the `healthnzrules` database.

## Setup and Pre-requisites

If not already installed:
Install Docker on your device (you can use the following link for a guide: https://docs.docker.com/get-docker/)
Install the latest version of OpenJDK 17 on your device (The following page has a complete catalogue of OpenJDK downloads: https://www.openlogic.com/openjdk-downloads)
Clone this repository or download the .zip file from GitHub (extract the downloaded zip file )


### Running the Microservice

1. Using a Command Line Interface of your choosing, change directory to the downloaded/cloned repository

2. Run the following command to build a .jar application file of the microservice:

<# Linux/MacOs #>
./mvnw clean package -DskipTests

<# Windows #>
.\mvnw clean package -DskipTests

If the build is a success, it should be indicated in your CLI. If it is successful, run this command to deploy it:
docker-compose up -d --build

3. If the build is a success, it should be indicated in your CLI. If it is successful, run this command to deploy it:
docker-compose up -d --build

4. Three containers should be running which are 
   db, pgadmin and microservice

