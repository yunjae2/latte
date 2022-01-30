#!/bin/bash

USERNAME=$1
PASSWORD=$2

sudo htpasswd -cmb /var/www/git/.htpasswd "$USERNAME" "$PASSWORD"