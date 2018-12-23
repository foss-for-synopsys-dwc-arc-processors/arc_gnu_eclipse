#!/bin/sh

# Supported environment variables:
#   MAVEN_FLAGS - options to pass to maven.

PREREQ=/u/arcgnu_verif/prerequisites
ECLIPSE_VERSION=2018-12

MAVEN=/u/arcgnu_verif/prerequisites/debugger/apache-maven-3.5.4/bin/mvn
MAVEN_LOCAL_REPO=$PREREQ/eclipse/$ECLIPSE_VERSION/maven_local_repo

JAVA_HOME=/u/arcgnu_verif/prerequisites/debugger/java/jdk-11
export PATH=$JAVA_HOME/bin:$PATH

$MAVEN \
	-Dmaven.repo.local=$MAVEN_LOCAL_REPO \
	$MAVEN_FLAGS \
	clean install
