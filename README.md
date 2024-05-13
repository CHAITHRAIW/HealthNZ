This Github repository contains a Springboot Microservice with a Postgres Database that can be dockerized:

Info-Fetcher-Microservice

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
If not already installed:
Install Docker on your device (you can use the following link for a guide: https://docs.docker.com/get-docker/)
Install the latest version of OpenJDK 17 on your device (The following page has a complete catalogue of OpenJDK downloads: https://www.openlogic.com/openjdk-downloads)
Clone this repository or download the .zip file from GitHub (extract the downloaded zip file )


Running the Microservice
Using a Command Line Interface of your choosing, change directory to the downloaded/cloned repository

Run the following command to build a .jar application file of the microservice:

<# Linux/MacOs #>
./mvnw clean package -DskipTests

<# Windows #>
.\mvnw clean package -DskipTests
If the build is a success, it should be indicated in your CLI. If it is successful, run this command to deploy it:
docker-compose up -d --build

7 docker containers should now be running:

microservice: where the spring-boot api image, built using a Dockerfile, is containerized
db: where a Postgres database is containerized and used by the API
kong-gateway: where a Kong API gateway is containerized to route the API
kong-db: where a Postgres database is containerized and used by the Kong gateway
kong-migration: where a temporary Kong API Gateway runs the kong migrations bootstrap command to initialize the kong-gateway container
pgadmin: where a pgAdmin container is used to access both Postgres databases we have containerized.
service-init: where a python script runs to set up our kong-gateway container with all the services and routes needed to access our microservice.
The microservice is now running, ready for use and is connected to the Kong API Gateway.

Verifying the containers are running
Microservice
Using a web browser of your choosing, head to http://localhost:8080/person/1. You should have accessed a microservice endpoint. Since the database it is accessing has not been populated yet, this endpoint will return the following with a 404 response:

{
    "timestamp": "{local time of response}",
    "message": [
        "The Person Object with id '1' does not exist in our records"
    ]
}
pgAdmin
Using a web browser of your choosing, head to http://localhost:5050/. You should see the pgAdmin login page. To verify both databases, make sure to sign in using the following credentials:

Email : admin@admin.com
Password : admin
You can change these credentials under the following pgadmin container environment variables in the "docker-compose.yaml" file:

PGADMIN_DEFAULT_EMAIL
PGADMIN_DEFAULT_PASSWORD
Both databases
After successfully logging into pgAdmin, click on Add New Server on the Dashboard Home Page:

In the General tab, name your server as you see fit
Navigate to the Connection tab
For the host name/address, use the name of the Postgres container db
Make sure the port field is 5432
the Username field is defined by the POSTGRES_USER environment variable for the db container in the docker-compose.yaml file
the Password field is defined by the POSTGRES_PASSWORD environment variable for the db container in the docker-compose.yaml file
Click save and, in the Object explorer, under Servers you should see your newly saved server db. This is the database the microservice uses.
To verify the kong database is up, we will add another server. Navigate to the Servers (1) element in the Object explorer. In the Dashboard tab, click on Add New Server on the Dashboard Home Page:

In the General tab, name your server as you see fit
Navigate to the Connection tab
For the host name/address, use the name of the Postgres container kong-database
Make sure the port field is 5432
the Username field is defined by the POSTGRES_USER environment variable for the kong-database container in the docker-compose.yaml file
the Password field is defined by the POSTGRES_PASSWORD environment variable for the kong-database container in the docker-compose.yaml file
Click save and, in the Object explorer, under Servers you should see your newly saved server kong-database. This is the database the API gateway uses.
Kong Container
Using a web browser of choice, head to http://localhost:8002/. You should have accessed the Kong Manager GUI web page.

Cleaning up Exited Containers
At this point, there are 2 container which are no longer running: kong-migration and service-init. These containers have only ran to initialize and populate the Kong API gateway. To remove these exited containers, run the following command in a seperate CLI window:

docker container prune -f

Populating the Microservice Database
In order to test our Microservice, we will need to populate the database it uses. Once all containers are up and running, run the following command, replacing "DATABASE_USERNAME" with the POSTGRES_USER environment variable value for the db container in the docker-compose.yaml file:

docker exec -it db psql -U "DATABASE_USERNAME" -d "DATABASE_USERNAME" -f /tmp/Mock_data.sql
This will fill our db postgres database container with 1000 Person entries from the file /db/Mock_data.sql.

Testing the Microservice
Import the Postman Collection provided as a .json file in the postman folder in this repository

Send the request from the endpoint "Get Person by ID"

You should get the following response body:

{
    "id": 1,
    "firstName": "Bob",
    "lastName": "Henderson",
    "age": 26,
    "sex": "Female"
}


