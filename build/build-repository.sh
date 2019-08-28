#!/bin/sh

# Supported environment variables:
#   MAVEN_FLAGS - options to pass to maven.

PREREQ=/u/svc-arcoss_auto/prerequisites
ECLIPSE_VERSION=2018-12

MAVEN=/u/svc-arcoss_auto/prerequisites/debugger/apache-maven-3.5.4/bin/mvn
MAVEN_LOCAL_REPO=$PREREQ/eclipse/$ECLIPSE_VERSION/maven_local_repo

JAVA_HOME=/u/svc-arcoss_auto/prerequisites/debugger/java/jdk-11
export PATH=$JAVA_HOME/bin:$PATH

$MAVEN \
	-Dmaven.repo.local=$MAVEN_LOCAL_REPO \
	$MAVEN_FLAGS \
	clean install
