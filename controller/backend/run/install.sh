#!/bin/bash

LATTE_DIR=${HOME}/.latte

mkdir -p "${LATTE_DIR}"
cp controller.yml "${LATTE_DIR}"/controller.yml &&
cp register_user.sh "${LATTE_DIR}"/register_user.sh &&

cd ../ &&
./gradlew build