#!/bin/sh -e

usage() {
  echo "Usage: $0 GIT_BRANCH"
  exit 1
}

if [ "$#" -ne 1 ]; then
  usage
fi

git_branch="$1"

sbt "reload:update"
./scripts/sbt-ci.sh "$EXTRA_SBT_ARGS"
