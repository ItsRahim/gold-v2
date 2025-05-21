#!/bin/bash

echo "🔍 Running SonarQube scan for Java modules..."

mvn clean verify sonar:sonar \
  -Dsonar.projectKey=RGTS \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=$SONAR_TOKEN