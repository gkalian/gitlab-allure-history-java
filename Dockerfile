FROM alpine:3.17.3

RUN apk update \
  && apk add --update --no-cache openjdk11 tzdata curl unzip bash git \
  && rm -rf /var/lib/apt/lists/*

ENV GROOVY_VERSION=4.0.7 \
    GROOVY_HOME="/home/groovy" \
    PATH="$PATH:/home/groovy/bin"

RUN curl -kLO https://archive.apache.org/dist/groovy/${GROOVY_VERSION}/distribution/apache-groovy-binary-${GROOVY_VERSION}.zip \
      && unzip "apache-groovy-binary-${GROOVY_VERSION}.zip" \
      && mv "/groovy-${GROOVY_VERSION}" "/home/groovy" \
      && rm apache-groovy-binary-${GROOVY_VERSION}.zip

ENV ALLURECMD_VERSION=2.22.0 \
    PATH="${PATH}:/home/allure-${ALLURECMD_VERSION}/bin"

RUN curl -kLO https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/${ALLURECMD_VERSION}/allure-commandline-${ALLURECMD_VERSION}.zip \
      && unzip "allure-commandline-${ALLURECMD_VERSION}.zip" \
      && mv "/allure-${ALLURECMD_VERSION}" "/home/allure-cmd" \
      && rm allure-commandline-${ALLURECMD_VERSION}.zip

COPY generate_index.groovy /home/groovy

WORKDIR /home/groovy