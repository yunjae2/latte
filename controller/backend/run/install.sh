#!/bin/bash

LATTE_DIR=${HOME}/.latte

cd ../ &&
./gradlew build &&

mkdir -p ${LATTE_DIR}
cp controller.yml ${LATTE_DIR}/controller.yml