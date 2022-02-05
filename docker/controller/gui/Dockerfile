FROM node:14-alpine

ENV FRONTEND_DIR="/root/frontend"
RUN mkdir -p ${FRONTEND_DIR}

COPY ./public/ ${FRONTEND_DIR}/public
COPY ./src/ ${FRONTEND_DIR}/src
COPY ./package.json ${FRONTEND_DIR}

WORKDIR ${FRONTEND_DIR}

# Install frontend
RUN npm install

# Start frontend
EXPOSE 80
ENTRYPOINT ["npm", "start"]
