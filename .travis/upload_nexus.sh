#!/bin/bash

# Pull requests shouldn't try to deploy
if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    echo "Skipping nexus upload; just doing a build."
    exit 0
fi

./gradlew uploadArchives uploadShadow -Psigning.password=$signingPassword -PnexusUsername=$sonatypeUsername -PnexusPassword=$sonatypePassword
