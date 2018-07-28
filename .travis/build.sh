#!/bin/bash

set -e # Exit with nonzero exit code if anything fails

if [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
    ./gradlew build
else
    ./gradlew build \
    -Penable.signing=true \
    -Psigning.keyId=D1115C87 \
    -Psigning.password=$signingPassword \
    -Psigning.secretKeyRingFile="$HOME/signing.gpg" \
    -PnexusUsername=$sonatypeUsername \
    -PnexusPassword=$sonatypePassword

    ./gradlew sonarqube \
    -Penable.signing=true \
    -Psigning.keyId=D1115C87 \
    -Psigning.password=$signingPassword \
    -Psigning.secretKeyRingFile="$HOME/signing.gpg" \
    -PnexusUsername=$sonatypeUsername \
    -PnexusPassword=$sonatypePassword

    ./gradlew publish \
    -Penable.signing=true \
    -Psigning.keyId=D1115C87 \
    -Psigning.password=$signingPassword \
    -Psigning.secretKeyRingFile="$HOME/signing.gpg" \
    -PnexusUsername=$sonatypeUsername \
    -PnexusPassword=$sonatypePassword
fi
