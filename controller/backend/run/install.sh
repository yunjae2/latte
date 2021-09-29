#!/bin/bash

LATTE_DIR=${HOME}/.latte

mkdir -p ${LATTE_DIR}
cp controller.yml ${LATTE_DIR}/controller.yml &&

cd ../ &&
./gradlew build