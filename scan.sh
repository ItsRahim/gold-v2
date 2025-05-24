#!/bin/bash

echo "🔍 Running SonarQube scan for Java modules..."

# Checking if SONAR_TOKEN env variable is set
if [ -z "${SONAR_TOKEN}" ]; then
  echo "SONAR_TOKEN" is not set. Unable to run SonarQube scan.
  exit 0
fi

# Checking if SonarQube endpoint is set
if [ -z "${SONAR_URL}" ]; then
  echo "SONAR_URL" is not set. Unable to run SonarQube scan.
  exit 0
fi

mvn clean verify sonar:sonar \
  -Dsonar.projectKey=RGTS \
  -Dsonar.host.url="$SONAR_URL" \
  -Dsonar.token="$SONAR_TOKEN"