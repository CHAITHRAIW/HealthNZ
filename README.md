# HealthNZ

## Summary

This repository contains a Java application that connects to a PostgreSQL database and lists all table names within the `public` schema in the `postgres` database. Additionally, it processes XML and DRL files, storing relevant information in the database tables `drool_files` and `drool_rules`.

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
Install the latest version of OpenJDK 17 on your device. The following page has a complete catalog of OpenJDK downloads: [OpenJDK Downloads](https://www.openlogic.com/openjdk-downloads).

### Clone the Repository
Clone this repository or download the .zip file from GitHub and extract the downloaded zip file.

### Create Tables in PostgreSQL
The following tables need to be created in the PostgreSQL database:

```sql
-- Drop the tables in the correct order to avoid foreign key constraint errors

-- Drop drool_rules table
DROP TABLE IF EXISTS public.drool_rules;

-- Drop drool_files table
DROP TABLE IF EXISTS public.drool_files;

-- Drop mule_flow table
DROP TABLE IF EXISTS public.mule_flow;

-- Table: public.mule_flow

CREATE TABLE IF NOT EXISTS public.mule_flow
(
    xmlfilename character varying COLLATE pg_catalog."default" NOT NULL,
    muleflowname character varying COLLATE pg_catalog."default",
    CONSTRAINT mule_flow_pkey PRIMARY KEY (xmlfilename)
);

-- Table: public.drool_files

CREATE TABLE IF NOT EXISTS public.drool_files
(
    fileid integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    filename character varying COLLATE pg_catalog."default",
    xmlfilename character varying COLLATE pg_catalog."default",
    CONSTRAINT drool_files_pkey PRIMARY KEY (fileid),
    CONSTRAINT drool_files_xmlfilename_fkey FOREIGN KEY (xmlfilename)
        REFERENCES public.mule_flow (xmlfilename) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: public.drool_rules

CREATE TABLE IF NOT EXISTS public.drool_rules
(
    ruleid integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    fileid integer NOT NULL,
    rulename character varying COLLATE pg_catalog."default",
    rulecontent text COLLATE pg_catalog."default",
    CONSTRAINT drool_rules_pkey PRIMARY KEY (ruleid),
    CONSTRAINT "drool_rules_FileID_fkey" FOREIGN KEY (fileid)
        REFERENCES public.drool_files (fileid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

```


### Running the Microservice
   Change Directory: Using a Command Line Interface (CLI) of your choice, change the directory to the downloaded/cloned repository.
   
   Build the Application:

~~~
   1. For Linux/MacOS:
   sh

   ./mvnw clean package -DskipTests

   2. For Windows:
   sh

   .\mvnw clean package -DskipTests

~~~

### If the build is successful, it should be indicated in your CLI.
Deploy the Application:
Run the following command to deploy the application using 

~~~
docker-compose up -d --build
~~~

### Verify Containers:
Two containers should be runninG db and pgadmin.

Run the Application.java file. The required rules will be stored in the drool_rules and drool_files tables.

Verify the Containers are Running
To verify that the containers are running, use the following command:

~~~
docker ps
~~~

### Populating the Microservice Database
To populate the database, ensure that the XML and DRL files are placed in the correct directories specified in the configuration:

XML files should be placed in the directory specified by drools.flow.directory (default: **C:\Temp\Drools\Flows**).

DRL files should be placed in the directory specified by drools.rules.directory (default: **C:\Temp\Drools\Rules**).

When the application runs, it will process these files and populate the drool_files and drool_rules tables accordingly.


### Testing the Microservice
You can test the microservice by ensuring that the application processes the XML and DRL files correctly and that the database tables **drool_files** and **drool_rules** are populated as expected.