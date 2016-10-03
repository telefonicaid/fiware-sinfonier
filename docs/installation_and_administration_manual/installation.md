# Installation
In this guide we suppose that we are using git for getting the code but you may want to have your own way to get it.
```sh
git clone git@github.com:telefonicaid/fiware-sinfonier.git
cd fiware-sinfonier
```

### Requirements
+ Ubuntu (14.04)
+ Java 7 (JDK included)
+ Python and pip (2.7) 
+ Zookeeper (3.4.6)
+ Storm (0.10.0)
+ Mongodb (2.6)
+ Maven (3.3.9)
+ Maven server (We recommend to use Artifactory)

### Minimum resources
+ 4 Nodes
+ 8GB RAM
+ 100GB HD
+ 4CPUs
+ Ubuntu (14.04)

### Processes list
+ Storm
    + nimbus process
    + supervisor process
    + ui process
    + logviewer process
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
    + Sinfonier 

### Installation zookeeper
We recommend use a production mode following zookeeper [guide](https://zookeeper.apache.org/doc/r3.4.6/zookeeperStarted.html)

### Installation of storm
We recommend use a production mode following storm [guide](http://storm.apache.org/2015/11/05/storm0100-released.html)
NOTE: Each supervisor node should have installed module's python dependencies.

### Mongodb [guide](https://docs.mongodb.com/v2.6/installation/)
You could set up mongodb by your requirements but it should be running.

### Maven [guide](http://maven.apache.org/install.html)

### Maven server:
We recommend to use Artifactory, but you can use any that you want.
[Artifactory guide](https://www.jfrog.com/confluence/display/RTF/Welcome+to+Artifactory)
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
In this guide we suppose that we are using git for getting the code but you may want to have your own way to get it. After get the code you will have to compile the code and push it to your maven repository. One time done this you can remove this proyect.

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