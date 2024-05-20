# HealthNZ

## Summary

This repository contains a Java application that connects to a PostgreSQL database and lists all table names within the `public` schema in the `postgres` database.

## Table of Contents

- [Setup and Pre-requisites](#setup-and-pre-requisites)
- [Running the Microservice](#running-the-microservice)
- [Verify the Containers are Running](#verify-the-containers-are-running)
- [Cleaning up Exited Containers](#cleaning-up-exited-containers)
- [Populating the Microservice Database](#populating-the-microservice-database)
- [Testing the Microservice](#testing-the-microservice)

## Setup and Pre-requisites

### Install Docker
If not already installed, install Docker on your device. You can use the following link for a guide: [Get Docker](https://docs.docker.com/get-docker/).

### Install OpenJDK 17
Install the latest version of OpenJDK 17 on your device. The following page has a complete catalogue of OpenJDK downloads: [OpenJDK Downloads](https://www.openlogic.com/openjdk-downloads).

### Clone the Repository
Clone this repository or download the .zip file from GitHub and extract the downloaded zip file.

### Create Tables in PostgreSQL
The following tables need to be created in the PostgreSQL database:

```sql
CREATE TABLE IF NOT EXISTS public.drool_files (
    "FileID" integer NOT NULL DEFAULT nextval('drool_files_FileID_seq'::regclass),
    "FileName" character varying COLLATE pg_catalog."default",
    CONSTRAINT drool_files_pkey PRIMARY KEY ("FileID")
);



CREATE TABLE IF NOT EXISTS public.drool_rules (
    "RuleID" integer NOT NULL DEFAULT nextval('drool_rules_RuleID_seq'::regclass),
    "FileID" integer NOT NULL,
    "RuleName" character varying COLLATE pg_catalog."default" NOT NULL,
    "RuleContent" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT drool_rules_pkey PRIMARY KEY ("RuleID"),
    CONSTRAINT "drool_rules_FileID_fkey" FOREIGN KEY ("FileID")
        REFERENCES public.drool_files ("FileID") MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

```

### Running the Microservice
   Change Directory: Using a Command Line Interface (CLI) of your choice, change the directory to the downloaded/cloned repository.
   
   Build the Application:

   1. For Linux/MacOS:
   sh

   ./mvnw clean package -DskipTests

   2. For Windows:
   sh

   .\mvnw clean package -DskipTests

### If the build is successful, it should be indicated in your CLI.
Deploy the Application:
Run the following command to deploy the application using 

docker-compose up -d --build

### Verify Containers:
Two containers should be runninG db and pgadmin.

Run thE Application.java file. The required rules will be stored in the drool_rules and drool_files tables.

Verify the Containers are Running
To verify that the containers are running, use the following command:

docker ps

