#!/bin/bash

jarfile=./target/json-organizer.jar

if [ ! -f "$jarfile" ]; then
  echo "********************************************************************************"
  echo "JAR file missing, running Maven build."
  echo "********************************************************************************"
  ./mvnw clean package
fi

java -jar $jarfile "$@"
