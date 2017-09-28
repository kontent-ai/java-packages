#!/bin/bash

# Pull requests and commits to other branches shouldn't try to deploy, just build to verify
if [ "$TRAVIS_PULL_REQUEST" != "false" -o "$TRAVIS_BRANCH" != "$SOURCE_BRANCH" ]; then
    echo "Skipping key unpack; just doing a build."
    exit 0
fi

openssl aes-256-cbc -K $encrypted_604254be91e3_key -iv $encrypted_604254be91e3_iv -in secrets.tar.enc -out secrets.tar -d
tar xvf secrets.tar
