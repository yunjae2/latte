#!/bin/bash

cd ../ &&

http-server build --port 80 --proxy http://localhost:8080/
