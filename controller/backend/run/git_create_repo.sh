#!/bin/bash
GIT_DIR="/var/www/git"
REPO_NAME=$1
USER=$2

mkdir -p "${GIT_DIR}/${REPO_NAME}"
cd "${GIT_DIR}/${REPO_NAME}"
git init &> /dev/null
cd .git/
cp hooks/post-update.sample hooks/post-update
git config http.receivepack true
git config http.uploadpack true
git config receive.denyCurrentBranch updateInstead
git update-server-info

groupadd latteusers
usermod -a -G latteusers apache
usermod -a -G latteusers "${USER}"

chgrp -R latteusers "${GIT_DIR}/${REPO_NAME}"
chmod -R 770 "${GIT_DIR}/${REPO_NAME}"
echo "Git repository '${REPO_NAME}' created in ${GIT_DIR}/${REPO_NAME}"
