#!/usr/bin/env bash

set -e

VERSION="$1"

if [ -z "$VERSION" ]
then
  echo "Please say which version you want to set"
  echo "Example:"
  echo "    $0 1.0.0"
  exit 1
fi

echo "[Version $VERSION] creating version"
./set-version.sh "$VERSION"

echo "[Version $VERSION] pushing new version"
git add .
git commit -m "starting version $VERSION"
git push

echo "[Tag $VERSION] creating tag"
git tag -a $VERSION -m "version $VERSION"

echo "[Tag $VERSION] pushing tag"
git push origin $VERSION
