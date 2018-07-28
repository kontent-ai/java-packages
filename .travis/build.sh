#!/bin/bash

set -e # Exit with nonzero exit code if anything fails
SOURCE_BRANCH="master"

if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_BRANCH" != "$SOURCE_BRANCH" ]; then
    ./gradlew build
else
    ./gradlew build \
    -Penable.signing=true \
    -Psigning.keyId=D1115C87 \
    -Psigning.password=$signingPassword \
    -Psigning.secretKeyRingFile="$TRAVIS_BUILD_DIR/signing.gpg" \
    -PnexusUsername=$sonatypeUsername \
    -PnexusPassword=$sonatypePassword

    ./gradlew sonarqube \
    -Penable.signing=true \
    -Psigning.keyId=D1115C87 \
    -Psigning.password=$signingPassword \
    -Psigning.secretKeyRingFile="$TRAVIS_BUILD_DIR/signing.gpg" \
    -PnexusUsername=$sonatypeUsername \
    -PnexusPassword=$sonatypePassword

    ./gradlew publish \
    -Penable.signing=true \
    -Psigning.keyId=D1115C87 \
    -Psigning.password=$signingPassword \
    -Psigning.secretKeyRingFile="$TRAVIS_BUILD_DIR/signing.gpg" \
    -PnexusUsername=$sonatypeUsername \
    -PnexusPassword=$sonatypePassword
fi
