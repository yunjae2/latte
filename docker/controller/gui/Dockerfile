ARG BUILD_DIR=/root

# Build the artifact
FROM node:14-alpine AS build
ARG BUILD_DIR
ENV BUILD_HOME=${BUILD_DIR}

WORKDIR ${BUILD_HOME}

COPY public/ public/
COPY src/ src/
COPY package.json .

## Fetch dependencies
RUN npm install

## Do the actual build
RUN npm run build


# Run the artifact
FROM node:14-alpine
ARG BUILD_DIR
ENV GUI_HOME=/root/gui

WORKDIR ${GUI_HOME}

COPY --from=build ${BUILD_DIR}/build/ .
COPY server/ server/

WORKDIR ${GUI_HOME}/server

## Install server
RUN npm install

## Start GUI server
EXPOSE 80
ENTRYPOINT ["node", "server.js"]
