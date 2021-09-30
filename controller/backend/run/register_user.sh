#!/bin/bash

USERNAME=$1
PASSWORD=$2

sudo htpasswd -cm /etc/svn/svn-auth "$USERNAME" "$PASSWORD"