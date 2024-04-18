#!/bin/sh -e
export NEXUS_USER=jenkins
export SBT_OPTS="-Xms2G -Xmx8G -Xss2M -XX:MaxMetaspaceSize=8G"
exec sbt "-Dsbt.ci=true" "$@"
