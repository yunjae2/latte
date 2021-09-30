#!/bin/bash

SVN_PORT=8082

echo "Installing packages.."
sudo yum -y install git httpd httpd-tools &&

echo "Configuring git settings.."
sudo cp git.conf /etc/httpd/conf.d/git.conf &&

sudo mkdir /var/www/git /etc/git &&

# Git repository
echo "Creating git repository.."
sudo ./git_create_repo.sh repository &&

# Git HTTP access auth
echo "Creating Git authorization.."
sudo touch /etc/git/git-auth &&
sudo chown root:apache /etc/git/git-auth &&
sudo chmod 640 /etc/git/git-auth &&

echo "Configuring Git port.."
sudo sed -i 's/^Listen.*/Listen '$SVN_PORT'/' /etc/httpd/conf/httpd.conf

sleep 10

# Run apache server for serving Git
echo "Starting Git server.."
sudo systemctl enable --now httpd
sudo systemctl restart httpd