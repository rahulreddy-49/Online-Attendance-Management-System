#!/usr/bin/env bash
set -e

# Render injects the port to listen on via $PORT; default to 8080 for local runs.
PORT="${PORT:-8080}"

# Point Tomcat's HTTP connector at the platform-provided port.
sed -i "s/port=\"8080\"/port=\"${PORT}\"/g" /usr/local/tomcat/conf/server.xml

exec catalina.sh run
