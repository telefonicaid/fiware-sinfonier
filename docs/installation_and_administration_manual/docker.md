# Docker
## Sinfonier Backend Docker 

### Introduction
As part of project Sinfonier. This Docker image installs the core backend library in maven repository. The container installs the library and finishes. This is provided as an example of how to install the library in the maven repository, and for an easy setup, but really it's not mandatory to use this container and may be better to install manually.

### Dependencies
It needs to have a maven repository to point to. The settings.xml provided has the credentials needed to point to the artifactory maven repository shown in docker-compose.yml

### Environment
You can redefine the following environment variables to change to your repository. Here are with its predefined values:
    ENV MVN_TARGET_HOST=artifactory
    ENV MVN_TARGET_PORT=8081
    ENV MVN_TARGET_USER=admin
    ENV MVN_TARGET_PASSWORD=password
    ENV MVN_TARGET_ID=central
    ENV MVN_TARGET_NAME=libs-release
    ENV MVN_TARGET_URL_PATH=artifactory/libs-release
    ENV MVN_TARGET_URL_LOCAL_PATH=artifactory/libs-release-local

## Sinfonier Backend Api Docker

### Introduction
As part of project Sinfonier. This Docker image raises a python server used by frontend to communicate with storm.

### Dependencies
It needs to have a maven repository to point to. The settings.xml provided has the credentials needed to point to the artifactory maven repository shown in docker-compose.yml
Also a storm process must be up and running to submit topologies and explore it's status

### Environment
* Configuration can be done using environment variables:
    * SINFONIER_API_HOST: ip where to raise the server
    * SINFONIER_API_HOST: port where to listen
    * STORM_UI_HOST: storm ui api host
    * STORM_UI_PORT: storm ui api port

* When code of modules is stored in Gist, a github user an token must be provided:
    * GIST_USERNAME
    * GIST_TOKEN

* Mongo configuration
    * MONGO_HOST
    * MONGO_PORT
    * MONGO_DATABASE: Database name
    * MONGO_AUTH: true or false. Indicates if auth is set
    * MONGO_USER
    * MONGO_PASSWORD

* Maven
    * INTERNAL_MVN_REPOSITORY: true or false. Indicates if uses an internal maven repository or works with local cache
    
If the previous variable is set to true, then you can define the location of repository with these variables:
* MVN_REPOSITORY_ID: maven repository id
* MVN_REPOSITORY_URL: Url to access the repository i.e.: http://artifactory:8081/artifactory/libs-release-local

## Sinfonier Web Docker 

* Introduction
As part of project Sinfonier. This Docker image starts the front part of the project. It is advised to use docker compose to 
start this image, because it has a lot of dependencies from other modules and services.

* Dependencies
It needs to have a mongodb database. You can change mongodb location changing the environment variable. By default is http://api
```sh
docker run  -e "STORM_HOST_ENV=http://localhost" sinfonierdocker_web
```
Also it needs to have a sinfonier-backend-api instance running:
Location URI can be overriden by an environment variable too. By default is mongodb://mongo:27017
```sh
docker run  -e "MONGO_URI_ENV=mongodb://mongo:27017" sinfonierdocker_web
```
