#!/bin/sh
# Maven Wrapper Script

set -e

# Resolve links
PRG="$0"
while [ -h "$PRG" ]; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`/"$link"
    fi
done

PRGDIR=`dirname "$PRG"`
BASE_DIR=`cd "$PRGDIR" >/dev/null; pwd`

# Maven wrapper properties
WRAPPER_PROPERTIES="$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
WRAPPER_JAR="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_HOME="$BASE_DIR/.mvn/maven"

# Download wrapper jar if not exists
if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Downloading Maven Wrapper..."
    WRAPPER_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"
    mkdir -p "$BASE_DIR/.mvn/wrapper"
    curl -sL -o "$WRAPPER_JAR" "$WRAPPER_URL"
fi

# Download Maven if not exists
if [ ! -d "$MAVEN_HOME" ]; then
    echo "Downloading Maven..."
    MAVEN_VERSION="3.9.6"
    MAVEN_URL="https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/${MAVEN_VERSION}/apache-maven-${MAVEN_VERSION}-bin.tar.gz"
    mkdir -p "$BASE_DIR/.mvn"
    curl -sL "$MAVEN_URL" | tar xz -C "$BASE_DIR/.mvn"
    mv "$BASE_DIR/.mvn/apache-maven-${MAVEN_VERSION}" "$MAVEN_HOME"
fi

exec "$MAVEN_HOME/bin/mvn" "$@"
