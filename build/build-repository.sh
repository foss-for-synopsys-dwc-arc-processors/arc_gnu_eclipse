#!/bin/sh

PREREQ=/u/arcgnu_verif/prerequisites
ECLIPSE_VERSION=neon-4.6
ECLIPSE_P2_REPO=$PREREQ/eclipse/$ECLIPSE_VERSION/p2_repository

MAVEN=$PREREQ/apache-maven-3.3.9/bin/mvn
MAVEN_LOCAL_REPO=$PREREQ/eclipse/$ECLIPSE_VERSION/maven_local_repo

JAVA_HOME=/depot/java-1.8.0_91
export PATH=$JAVA_HOME/bin:$PATH

$MAVEN \
	-Dmaven.repo.local=$MAVEN_LOCAL_REPO \
	-Declipse.repository=file:$ECLIPSE_P2_REPO \
	-o clean install
