# DevOps

CI: 持续集成 
- code--build--test

CD（Delivery）:持续交付
- 监控提交代码
- 打包发送到产品库
  
CD(Deployment)：持续部署
- 监控入库产品
- 将产品库中打包的按要求部署上线
  
# zabbix

wget https://repo.zabbix.com/zabbix/4.0/ubuntu/pool/main/z/zabbix-release/zabbix-release_4.0-2+bionic_all.deb
wget https://repo.zabbix.com/zabbix/4.0/ubuntu/pool/main/z/zabbix-release/zabbix-release_4.0-2+xenial_all.deb
sudo dpkg -i zabbix-release_4.0-2+bionic*.deb
sudo dpkg -i zabbix-release_4.0-2+xenial*.deb

https://websiteforstudents.com/how-to-install-zabbix-4-0-monitoring-system-with-apache2-mariadb-and-php-7-2-on-ubuntu-16-04-18-04-18-10/

# ansible