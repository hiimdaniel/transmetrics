# Transmetrics coding assignment

### Summary ###
This application aims to query holiday data from xmltime API, store it in a local DB and serve it on different Rest endpoints.

### How to start the application locally? ###
There are two approaches to start the application:
*For simple local running please fill out the _accesskey_ and _secret_key_ parameters in the [application-dev.yml file](src\main\resources\application-dev.yml),
 start the [docker-compose file](src\test\resources\docker\docker-compose.yml) from test-resources and start the application with "dev" profile. 
 This way the application will use your API credentials to make requests to the xmltime API and persist the result in a local Postgres DB which could be
 accessed on localhost:5432. After the startup the application is available on 8080 port.
*For a containerized local running please fill out the missing details in the [application-dev.yml file](src\main\resources\application-dev.yml),
 and initialize a _mvn clean install_ (or feel free to use the included Maven wrapper). In this case a configured _exec-maven-plugin_ will
 create a new Docker image in your local repository (it will not use or access your remote docker-hub). By issuing a _docker-compose up_ command
 in the project root directory one can start a dockerized environment with a Postgres DB connection in "dev" profile as described in the [docker-compose file](docker-compose.yml)
 in the root directory.

### Local testing ###
After a successful application startup the application main page is available at http://localhost:8080/ .
A brief description about the available endpoints could be found on the project's [Swagger UI page](http://localhost:8080/swagger-ui/).

### Good to know ###
*The application fetches data from the API on demand, meaning it will first check if we already have the requested data in the local DB and initiating a 
request only if we don't. It could cause problems with huge API responses, but I find it safe in this scenario.
*There are 3 different Rest endpoints in this application for serving requested holiday data. A simple, a paginated, and a stream-based response could also be requested.
I thought in a real life scenario it could be useful. 


### Missing features and Known issues ###
#### Invalid signature error ####
For some reason the  xmltime API returns an invalid signature response in some cases. After a few hours of investigation I failed to identify the root cause of the issue
and I couldn't reproduce it. I suspect that under the hood the API is using a different approach to calculate the signature HMAC-SHA1 value, but I'm just guessing.
If a long term usage of the API was planned then I would implement a client library to make it more simple, but of course it was not part of this exercise.

#### Too specific mapping ####
I implemented only the mapping, and the representation of the "holiday" response from the API, ignoring every possible other response types.
Sadly, due to the not-so-descriptive API documentation I often used trial by error approaches to figure out the response format which led to
not so elegant mapping solutions. On the long run, an API client library could resolve this issue as well.

#### Decoupling of command and query ####
Generally I think that a CQRS pattern should be applied during the development of a project like this but as my task was only to 
fetch the holidays of a given country for a given year and serve it on Rest endpoint as-it-is I decided not decoupling the 
entities from the Rest response objects. 

#### Paginated response - next page ####
I decided to include a paginated endpoint as well to make it easier to consumer large amount of data. Defining a well usable paginated 
entry could be really tricky, and I have to admit that there are holes in my implementation, for example the host of the next page URI
will always be "localhost". 