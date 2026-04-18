#!/usr/bin/env bash

git update-index --refresh && git diff-index --quiet HEAD --
if [ $? -ne 0 ]; then echo "uncommitted changes, aborting"; exit 1; fi

git checkout gh-pages
git rebase master
bb generate
git add -A
git commit -m "updated: $(date +'%Y-%m-%d %H:%M:%S')"
git push origin gh-pages
git checkout -
