#!/bin/bash
if [ "${RELEASE}" = "true" ]; then
./gradlew uploadArchives -Psigning.password=$signingPassword -PnexusUsername=$sonatypeUsername -PnexusPassword=$sonatypePassword
fi
