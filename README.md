# Fiware-sinfonier-docs

## Installation
In this guide we suppose that we are using git for getting the code but you may want to have your own way to get it.
```sh
git clone git@github.com:telefonicaid/fiware-sinfonier.git
cd fiware-sinfonier
```

### Requirements
    * Ubuntu (14.04)
    * Java 7 (JDK included)
    * Python and pip (2.7) 
    * Zookeeper (3.4.6)
    * Storm (0.10.0)
    * Mongodb (2.6)
    * Maven (3.3.9)
    * Maven server (We recommend to use Artifactory)
    
### Minimum resources (The following section was intended for production environments)
    * 4 Nodes
    * 8GB RAM
    * 100GB HD
    * 4CPUs
    * Ubuntu (14.04)

### Processes list
+ Storm
    + nimbus process
    + supervisor process
    + ui process
    + Logviewer process
+ Zookeeper
    + zookeeper
+ Mongo
    + mongod
+ Sinfonier Web
    + play
+ Sinfonier API
    + gunicorn
+ Artifactory
    + artifactory

### Database List
+ Mongo
    + sinfonier

### Installation zookeeper
We recommend use a production mode following zookeeper [guide](https://zookeeper.apache.org/doc/r3.4.6/zookeeperStarted.html)

### Installation of storm
We recommend use a production mode following storm [guide](http://storm.apache.org/2015/11/05/storm0100-released.html)
NOTE: Each supervisor node should have installed module's python dependencies.

### Mongodb [guide](https://docs.mongodb.com/v2.6/installation/)
You could set up mongodb by your requirements but it should be running.

### Maven [guide](http://maven.apache.org/install.html)

### Maven server:
We recommend to use Artifactory, but you can use any that you want. [Artifactory guide](https://www.jfrog.com/confluence/display/RTF/Welcome+to+Artifactory)
We do not recommend to use the default configuration and add a new user with the right permissions. After to install and configured it you should configure your settings.xml for maven. The next code could be useful for you if you defined a user called sinfonier. 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.1.0 http://maven.apache.org/xsd/settings
-1.1.0.xsd" xmlns="http://maven.apache.org/SETTINGS/1.1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
  <servers>
    <server>
      <id>central</id>
        <username>sinfonier</username>
	<password>sinfonier</password>
    </server>
  </servers>
  <profiles>
    <profile>
      <repositories>
        <repository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>libs-release</name>
          <url>http://yourhost:8081/artifactory/libs-release</url>
        </repository>
      </repositories>
      <id>artifactory</id>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>artifactory</activeProfile>
  </activeProfiles>
</settings>
```

### sinfonier-backend
After get the code you will have to compile the code and push it to your maven repository. One time done this you can remove this project.

```sh
cd path/to/fiware-sinfonier/sinfonier-backend
mvn clean compile package
mvn deploy:deploy-file -Dfile=target/sinfonier-backend-1.1.0.jar -DgroupId=com.sinfonier -DartifactId=sinfonier-backend -Dversion=1.1.0 -Dpackaging=jar -DrepositoryId=central -Durl=http://yourmavenserver/artifactory/libs-release-local
```

### sinfonier-backend-api
This allow to communicate the both main components, the web and  apache storm. For this project you must configure a few things.

+ The host or hosts in which sinfonier-backend-api is going to be deployed must contain a tarball of Apache Storm and its location should be set through a configuration file or the STORM_HOME environment variable.
+ The host or hosts in which sinfonier-backend-api is going to be deployed must contain a tarball of Maven and its location should be set through a configuration file or the MAVEN_HOME environment variable.
+ Others things that you can set up are for example Mongodb, Gist or Logger but those should be in the config file.

NOTE: If you want to deployed using high availability you can use gunicorn. Take a look to their [guide](http://docs.gunicorn.org/en/stable/deploy.html)      

```sh
cd cd path/to/fiware-sinfonier/sinfonier-backend-api
pip install -r requirements
# dev mode
# python sinfonierapi.py
# prod mode using gunicorn 
gunicorn sinfonierapi:app
```

### sinfonier-web
You may want to deployed using high availability, so in this case you can use [nginx](https://nginx.org/)
By default you can register with `admin@localhost.com`. Also, you can define the default admin user and register with it changing in `conf/darwin.conf` the following values `auto_activated_users=your@email.com` and `auto_admin_users=your@email.com`. For more info take a look in [Darwin](https://github.com/ElevenPaths/darwin)

```sh
cd cd path/to/fiware-sinfonier/sinfonier-frontend
play dependencies --sync
play start
```
For checking if everything is alright visit [diagnosing](http://localhost:9000/diagnosis/) [http://localhost:9000/diagnosis]

### Global architecture and default ports (all port using by HTTP)
![Figure 1 - Sinfonier architecture Simple View](https://raw.githubusercontent.com/telefonicaid/fiware-sinfonier/master/resources/images/arquitecturaSinfonier.png "Sinfonier architecture Simple View")


### Docker
#### Sinfonier Backend Docker 

##### Introduction
As part of project Sinfonier. This Docker image installs the core backend library in maven repository. The container installs the library and finishes. This is provided as an example of how to install the library in the maven repository, and for an easy setup, but really it's not mandatory to use this container and may be better to install manually.

##### Dependencies
It needs to have a maven repository to point to. The settings.xml provided has the credentials needed to point to the artifactory maven repository shown in docker-compose.yml

##### Environment
You can redefine the following environment variables to change to your repository. Here are with its predefined values:
    ENV MVN_TARGET_HOST=artifactory
    ENV MVN_TARGET_PORT=8081
    ENV MVN_TARGET_USER=admin
    ENV MVN_TARGET_PASSWORD=password
    ENV MVN_TARGET_ID=central
    ENV MVN_TARGET_NAME=libs-release
    ENV MVN_TARGET_URL_PATH=artifactory/libs-release
    ENV MVN_TARGET_URL_LOCAL_PATH=artifactory/libs-release-local

#### Sinfonier Backend Api Docker

##### Introduction
As part of project Sinfonier. This Docker image raises a python server used by frontend to communicate with storm.

##### Dependencies
It needs to have a maven repository to point to. The settings.xml provided has the credentials needed to point to the artifactory maven repository shown in docker-compose.yml
Also a storm process must be up and running to submit topologies and explore it's status

##### Environment
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

#### Sinfonier Web Docker 

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

## Project leads

* Rodolfo Bordón Villar <rodolfo.bordon@11paths.com>

## Committers
* Alejandro Matos Caballero @amatosc https://github.com/amatosc/
* Alberto J. Sanchez @ajsanchezsanz https://github.com/ajsanchezsanz
* Fernando Andrés Rodriguez <fandrod@aspgems.com>
* Iván Ramos Muñoz <ivan.ramos@11paths.com>
* Jose Miguel Díez de la Lastra <jmdiez@aspgems.com>
* Jesús Torres @velatorre https://github.com/velatorre

## Contributors

## License

Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0

