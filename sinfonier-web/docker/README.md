# Sinfonier Web Docker 

## Introduction

As part of project Sinfonier. This Docker image starts the front part of the project. It is adviced to use docker compose to 
start this image, because it has a lot of dependencies from other modules and services.

## Dependencies

It needs to have a mongodb database. You can change mongodb location changing the environment variable. By default is http://api
    docker run  -e "STORM_HOST_ENV=http://localhost" sinfonierdocker_web
Also it needs to have a sinfonier-backend-api instance running:
Location URI can be overriden by an environment variable too. By default is mongodb://mongo:27017
    docker run  -e "MONGO_URI_ENV=mongodb://mongo:27017" sinfonierdocker_web

