#!/usr/bin/env bash

# set -e -o pipefail

PROJECT_ROOT="/var/www/Todoary-Server"

echo -e "\
 +------------------------------------+
 |              1. build              |
 +------------------------------------+"

echo -e "\
 +------- git pull... "
cd $PROJECT_ROOT || exit 1
git reset --hard origin/Develop
git pull origin Develop

echo -e "\
 +------- build... "
./gradlew clean build -x test


