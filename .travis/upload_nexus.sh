#!/bin/bash
./gradlew uploadArchives -Psigning.password=$signingPassword -PnexusUsername=$sonatypeUsername -PnexusPassword=$sonatypePassword
./gradlew closeAndReleaseRepository -PnexusUsername=$sonatypeUsername -PnexusPassword=$sonatypePassword
