#!/bin/bash

RATE=$1
DURATION=$2
VUS=$3
MAX_VUS=$4

CONFIG_FILE="scripts/TestHelper.js"

if [[ "$OSTYPE" == "linux-gnu"* ]]; then
  EXT_ARG=""
elif [[ "$OSTYPE" == "darwin"* ]]; then
  EXT_ARG="''"
fi

sed -i $EXT_ARG "s/rate:.*/rate: $RATE,/g" $CONFIG_FILE
sed -i $EXT_ARG "s/duration:.*/duration: '$DURATION',/g" $CONFIG_FILE
sed -i $EXT_ARG "s/preAllocatedVUs:.*/preAllocatedVUs: $VUS,/g" $CONFIG_FILE
sed -i $EXT_ARG "s/maxVUs:.*/maxVUs: $MAX_VUS,/g" $CONFIG_FILE
