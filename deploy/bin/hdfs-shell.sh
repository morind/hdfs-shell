#!/bin/bash
CWD=$(cd $(dirname $0); pwd)  
#JAVA_HOME=/opt/java8

${JAVA_HOME}/bin/java -Xms200m -Xmx400m -cp ${CWD}/lib/*:/opt/hadoop/etc/hadoop com.avast.server.hdfsshell.MainApp "$@"
