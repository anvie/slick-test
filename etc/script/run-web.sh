#!/usr/bin/env bash

if [ ! -z "$DEBUG" ]; then
    export SBT_OPTS="$SBT_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8989 "
fi

sbt "zufaro-web/container:launch"
