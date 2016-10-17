# Sinfonier Backend Api Docker

## Introduction

As part of project Sinfonier. This Docker image raises a python server used by frontend to communicate with storm.

## Dependencies

It needs to have a maven repository to point to. The settings.xml provided has the credentials needed to point to the artifactory maven repository shown in docker-compose.yml
Also a storm process must be up and running to submit topologies and explore it's status

## Environment

Configuration can be done using environment variables:

    SINFONIER_API_HOST: ip where to raise the server
    SINFONIER_API_HOST: port where to listen

    STORM_UI_HOST: storm ui api host
    STORM_UI_PORT: storm ui api port

When code of modules is stored in Gist, an github user an token must be provided:

    GIST_USERNAME
    GIST_TOKEN

Mongo configuration

    MONGO_HOST
    MONGO_PORT
    MONGO_DATABASE: Database name
    MONGO_AUTH: true or false. Indicates if auth is set
    MONGO_USER
    MONGO_PASSWORD

Maven

    INTERNAL_MVN_REPOSITORY: true or false. Indicates if uses an internal maven repository or works with local cache

If the previous variable is set to true, then you can define the location of repository with these variables:

    MVN_REPOSITORY_ID: maven repository id
    MVN_REPOSITORY_URL: Url to access the repository i.e.: http://artifactory:8081/artifactory/libs-release-local


