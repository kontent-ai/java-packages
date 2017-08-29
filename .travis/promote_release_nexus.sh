#!/bin/bash
./gradlew closeAndReleaseRepository -PnexusUsername=$sonatypeUsername -PnexusPassword=$sonatypePassword
