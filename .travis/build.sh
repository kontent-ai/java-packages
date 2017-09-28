#!/bin/bash

./gradlew build

# Pull requests shouldn't try to run sonarqube as travis doesn't configure it correctly when doing a PR from another remote
if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    echo "Skipping sonarqube; just doing a build."
    exit 0
fi

./gradlew sonarqube
