ARG BUILD_DIR=/home/gradle/src

# Build the artifact
FROM gradle:7.2-jdk11-alpine AS build
ARG BUILD_DIR
ENV GRADLE_HOME=${BUILD_DIR}
COPY --chown=gradle:gradle . ${GRADLE_HOME}
WORKDIR ${GRADLE_HOME}
RUN gradle build --no-daemon

# Run the artifact
FROM openjdk:11-jdk
ARG BUILD_DIR
ARG SCRIPT_DIR=scripts
ARG HELPER_SCRIPT=update_parameters.sh
ARG JAR_FILE=build/libs/*.jar

ENV LATTE_DIR="/root/.latte"
COPY --from=build ${BUILD_DIR}/${JAR_FILE} app.jar

RUN mkdir -p ${LATTE_DIR}
COPY ${SCRIPT_DIR}/${HELPER_SCRIPT} ${LATTE_DIR}
RUN chmod +x ${LATTE_DIR}/${HELPER_SCRIPT}

RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
RUN echo "deb https://dl.k6.io/deb stable main" | tee /etc/apt/sources.list.d/k6.list
RUN apt-get update
RUN apt-get install k6

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]