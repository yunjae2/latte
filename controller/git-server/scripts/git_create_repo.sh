#!/bin/bash

GIT_DIR="/var/www/git"
REPO_NAME=$1

mkdir -p "${GIT_DIR}/${REPO_NAME}"
cd "${GIT_DIR}/${REPO_NAME}"

git init
cd .git/
touch git-daemon-export-ok
cp hooks/post-update.sample hooks/post-update
git config receive.denyCurrentBranch updateInstead
git config http.receivepack true
git config http.uploadpack true
git update-server-info
chown -Rf www-data:www-data "${GIT_DIR}/${REPO_NAME}"
echo "Git repository '${REPO_NAME}' created in ${GIT_DIR}/${REPO_NAME}"