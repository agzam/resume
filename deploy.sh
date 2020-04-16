#!/usr/bin/env bash

git update-index --refresh && git diff-index --quiet HEAD --
if [ $? -ne 0 ]; then exit; fi

clojure -m resume.content
cp target/* .
cp resources/public/* .
git add -A
git stash push -m "deploy"
git checkout gh-pages
git stash pop "deploy"
git commit -m "$date +"%Y-%m-%d""
git push origin gh-pages
git checkout -
