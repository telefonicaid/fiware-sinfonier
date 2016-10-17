#!/bin/bash
sleep 30
echo "$(date) - Starting installation of sinfonier-backend library"
result=1
until [ "$result" == "0" ]
do
    mvn deploy:deploy-file -Dfile=target/sinfonier-backend-1.1.0.jar -DgroupId=com.sinfonier -DartifactId=sinfonier-backend -Dversion=1.1.0 -Dpackaging=jar -DrepositoryId=$MVN_TARGET_ID -Durl=http://$MVN_TARGET_HOST:$MVN_TARGET_PORT/$MVN_TARGET_URL_LOCAL_PATH
    result=$?
    echo "status $result"
    sleep 30
done


