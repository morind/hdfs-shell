#!/bin/bash
CWD=$(cd $(dirname $0); pwd)  
#JAVA_HOME=/opt/java8

nohup ${JAVA_HOME}/bin/java -Ddaemon=true -Xms200m -Xmx400m -cp ${CWD}/lib/*:/etc/hadoop/conf com.avast.server.hdfsshell.MainApp "$@" 2>&1 > /dev/null &
