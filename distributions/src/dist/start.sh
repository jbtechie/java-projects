#!/bin/sh

cd $(dirname $0)
java -jar lib/*.jar server config/server.yml