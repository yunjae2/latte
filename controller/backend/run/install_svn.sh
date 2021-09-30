#!/bin/bash

SVN_PORT=8082

echo "Installing packages.."
sudo yum -y install subversion mod_dav_svn &&

echo "Configuring subversion settings.."
sudo cp subversion.conf /etc/httpd/conf.d/subversion.conf &&

sudo mkdir /var/www/svn /etc/svn &&

# SVN repository
echo "Creating subversion repository.."
cd /var/www/svn &&
sudo svnadmin create repository &&
sudo chown -R apache:apache repository &&

# SVN HTTP access auth
echo "Creating subversion authorization.."
sudo touch /etc/svn/svn-auth &&
sudo chown root:apache /etc/svn/svn-auth &&
sudo chmod 640 /etc/svn/svn-auth

echo "Configuring SVN port.."
sudo sed -i 's/^Listen.*/Listen '$SVN_PORT'/' /etc/httpd/conf/httpd.conf

# Run apache server for serving SVN
echo "Starting SVN server.."
sudo systemctl enable --now httpd
sudo systemctl restart httpd