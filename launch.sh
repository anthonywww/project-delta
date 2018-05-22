#!/bin/bash

# Set the directory to this script's current directory
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Launch executable
java -jar ${DIR}/dist/compiled.jar 0 0
