#!/bin/bash
if [ "${RELEASE}" = "true" ]; then
./gradlew closeAndReleaseRepository -PnexusUsername=$sonatypeUsername -PnexusPassword=$sonatypePassword
fi
