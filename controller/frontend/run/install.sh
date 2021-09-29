#!/bin/bash

cd ../ &&

curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.34.0/install.sh | bash &&
source ~/.nvm/nvm.sh &&
nvm install node &&

if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    sudo setcap cap_net_bind_service=+ep $(which node)
fi

npm install &&
npm run build &&
npm install -g http-server
