#!/bin/sh

cd $(dirname $0)
java -Xms4g -Xmx4g -jar lib/*.jar server config/server.yml