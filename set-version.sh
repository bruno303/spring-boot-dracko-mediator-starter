#!/usr/bin/env bash

set -e

VERSION="$1"
VARIABLE_NAME="projectVersion"

if [ -z "$VERSION" ]
then
  echo "Please say which version you want to set"
  echo "Example:"
  echo "    $0 1.0.0"
  exit 1
fi

sed -i -E "s/(^def\\s$VARIABLE_NAME\\s=\\s['\"]).*/def $VARIABLE_NAME = '$VERSION'/" build.gradle