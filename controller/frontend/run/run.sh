#!/bin/bash

cd ../ &&

ws --spa index.html --directory build --port 80 --rewrite '/api/(.*) -> http://localhost:8080/$1'