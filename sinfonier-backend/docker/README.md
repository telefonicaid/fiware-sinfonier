# Sinfonier Backend Docker 

## Introduction

As part of project Sinfonier. This Docker image installs the core backend library in maven repository. The container installs the library and finishes. This is provided as an example of how to install the library in the maven repository, and for an easy setup, but really it's not mandatory to use this container and may be better to install manually.

## Dependencies

It needs to have a maven repository to point to. The settings.xml provided has the credentials needed to point to the artifactory maven repository shown in docker-compose.yml

## Environment

You can redefine the following environment variables to change to your repository. Here are with its predefined values:
	ENV MVN_TARGET_HOST=artifactory
	ENV MVN_TARGET_PORT=8081
	ENV MVN_TARGET_USER=admin
	ENV MVN_TARGET_PASSWORD=password
	ENV MVN_TARGET_ID=central
	ENV MVN_TARGET_NAME=libs-release
	ENV MVN_TARGET_URL_PATH=artifactory/libs-release
	ENV MVN_TARGET_URL_LOCAL_PATH=artifactory/libs-release-local