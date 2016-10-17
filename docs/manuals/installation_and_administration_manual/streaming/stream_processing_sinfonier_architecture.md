#<a name="top"></a>Sinfonier Architecture 

Content:<br>

* [Architecture Overview](#section1)
* [Components](#section2)

##<a name="section1"></a>Architecture Overview

Sinfonier has three main components:

* Drag & Drop interface where user can define Topologies, add new modules (Spouts, Bolts and Drains) and manage Apache Storm Cluster. (Sinfonier-Drawer)
* Integration API to join Apache Storm Cluster with FrontEnd interface (Sinfonier-API) and all related modules: Dependencies (Apache Maven and Python PIP), Classpath and project packaging. (Sinfonier-Backend)
* Apache Storm cluster. Sinfonier works using clean Apache Storm deploy.

![Figure 1 - Sinfonier Overview](images/sinfonier-overview.png "Figure 1 - Sinfonier Overview")

Sinfonier is partitioned in order to interact with each of the parts of the project independently. Sinfonier Backend is an independent project managed by Sinfonier API.

[Top](#top)

##<a name="section2"></a>Components

### Sinfonier Drawer

FrontEnd part of Sinfonier Project. Allow users to define Apache Storm Topologies (DAG - Directed acyclic graph) in a visual way and send it to Storm Cluster using [Sifonier API](https://github.com/telefonicaid/fiware-sinfonier/sinfonier-backend-api).

#### Technologies

* [mongoDB](http://www.mongodb.org/) (>= 2)

#### Integrated

Sinfonier Drawer use:

* Twitter
* [Latch](https://latch.elevenpaths.com/)

#### Technologies

* [Tornado](http://www.tornadoweb.org/en/stable/)
* [mongoDB](http://www.mongodb.org/) (>= 2)

### Sinfonier BackEnd

Sinfonier BackEnd allow to deploy Apache Storm Topologies defined using XML into Apache Storm Cluster. It's the final step on Sinfonier Project architecture and It's used by [Sinfonier API](https://github.com/telefonicaid/fiware-sinfonier/sinfonier-backend-api).

Sinfonier BackEnd use [Apache Maven](https://maven.apache.org/) to manage Java dependencies. Python dependencies must be solved using Python Pip.

#### Technologies

* [Java](https://www.java.com/es/download/help/index_installing.xml?j=7)
* [Apache Maven](https://maven.apache.org/)

### Apache Storm

Apache Storm is a free and open source distributed realtime computation system
Storm makes it easy to reliably process unbounded streams of data, doing for realtime processing what Hadoop did for batch processing. Storm is simple, can be used with any programming language, and is a lot of fun to use!.