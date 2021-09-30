#!/bin/bash

USERNAME=$1
PASSWORD=$2

sudo htpasswd -cmb /etc/git/git-auth "$USERNAME" "$PASSWORD"