#!/usr/bin/env bash

git update-index --refresh && git diff-index --quiet HEAD --
if [ $? -ne 0 ]; then exit; fi

git checkout gh-pages
git rebase master
clojure -m resume.app
cp target/* .
cp resources/public/* .
git add -A
git commit -m "updated: $(date +'%Y-%m-%d %H:%M:%S')"
git push origin gh-pages
git checkout -
