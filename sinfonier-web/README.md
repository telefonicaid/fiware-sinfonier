# Sinfonier Drawer

FrontEnd part of Sinfonier Project. Allow users to define Apache Storm Topologies (DAG - Directed acyclic graph) in a visual way and send it to Storm Cluster using [Sifonier API](https://github.com/telefonicaid/fiware-sinfonier/tree/master/sinfonier-web)

## Requisites

 * [Playframework](http://playframework.com) (= 1.4)
 * [mongoDB](http://www.mongodb.org/) (>= 2)

## Install

    ```sh
    git clone git@github.com:telefonicaid/fiware-sinfonier.git
    cd fiware-sinfonier/sinfonier-web
    play dependencies --sync
    ```

## Start

* Ensure you have a MongoDB running on localhost
* Start play app

    ```sh
    cd /path/to/sinfonier-frontend
    play run
    ```

* Browse this URL [http://localhost:9000](http://localhost:9000)
* By default you can register with `admin@localhost.com`. Also, you can define the default admin user and
* register with it changing in `conf/darwin.conf` the following values `auto_activated_users=your@email.com` and `auto_admin_users=your@email.com`. For more info
* take a look in Darwin(https://github.com/ElevenPaths/darwin)

## Integrated

Sinfonier Drawer use
* Latch
    - Sinfonier Drawer can be integrated with [Latch](https://latch.elevenpaths.com/). Before configure it it's necessary to create a new application and put Name, ID and Secret parameters in **config/darwin.conf** file.

## Project leads

* Rodolfo Bordón Villar <rodolfo.bordon@11paths.com>

## Committers
* Fernando Andrés Rodriguez <fandrod@aspgems.com>
* Iván Ramos Muñoz <ivan.ramos@11paths.com>
* Alejandro Matos Caballero @amatosc https://github.com/amatosc/

## Contributors

## License

Apache License, Version 2.0: http://www.apache.org/licenses/LICENSE-2.0
