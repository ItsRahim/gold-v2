#!/bin/bash

directories=("rgts-common" "rgts-kafka-service" "rgts-proto-service" "rgts-pricing-service")

for dir in "${directories[@]}"; do
  echo "Entering $dir and running mvn -B com.spotify.fmt:fmt-maven-plugin:format"

  if [ -d "$dir" ]; then
    cd "$dir" || exit
    mvn -B com.spotify.fmt:fmt-maven-plugin:format
    if [ $? -ne 0 ]; then
      echo "Maven format failed in $dir"
      exit 1
    fi
    cd ..
  else
    echo "Directory $dir does not exist"
    exit 1
  fi
done

exit 0
