ARG BUILD_DIR=/home/gradle/src

# Build the backend artifact
FROM gradle:7.2-jdk11-alpine AS build
ARG BUILD_DIR
ENV GRADLE_HOME=${BUILD_DIR}

## Fetch dependencies
COPY --chown=gradle:gradle build.gradle settings.gradle ${GRADLE_HOME}/
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

## Required for test ..
ENV LATTE_DIR=/root/.latte
RUN mkdir -p ${LATTE_DIR}
COPY ./scripts/controller.yml ${LATTE_DIR}

## Do the actual build
COPY --chown=gradle:gradle . ${GRADLE_HOME}
WORKDIR ${GRADLE_HOME}
RUN gradle build --no-daemon


# Run the artifact
FROM openjdk:11-jdk

ENV GIT_SOURCE_DIR=/var/www/git

ENV LATTE_DIR=/root/.latte
RUN mkdir -p ${LATTE_DIR}

## Install artifact
ARG BUILD_DIR
ARG JAR_FILE=build/libs/*.jar
COPY --from=build ${BUILD_DIR}/${JAR_FILE} app.jar

## Create initial config
COPY ./scripts/controller.yml ${LATTE_DIR}

## Install htpasswd required for git auth configuration
RUN apt-get update
RUN apt-get install apache2-utils
COPY ./scripts/register_user.sh ${LATTE_DIR}

## Finally run the artifact
VOLUME ${LATTE_DIR}
VOLUME ${GIT_SOURCE_DIR}
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]