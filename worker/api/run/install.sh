#!/bin/bash

cd ../ &&
./gradlew build &&

if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    wget https://github.com/grafana/k6/releases/download/v0.34.1/k6-v0.34.1-linux-amd64.rpm &&
    sudo yum -y install k6-v0.34.1-linux-amd64.rpm
elif [[ "$OSTYPE" == "darwin"* ]]; then
    brew install k6
fi
