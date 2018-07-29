#!/bin/bash

# Pull requests shouldn't try to deploy
if [ "${TRAVIS_PULL_REQUEST}" != "false" ]; then
    echo "Skipping key unpack; just doing a build."
    exit 0
fi

openssl aes-256-cbc -K ${encrypted_604254be91e3_key} -iv ${encrypted_604254be91e3_iv} -in secrets.tar.enc -out secrets.tar -d
tar xvf secrets.tar
