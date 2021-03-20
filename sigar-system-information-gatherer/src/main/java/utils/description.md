+ Sigar là viết tắt của System Information Gatherer And Reporter

+ Thu thập thông tin về machine device run service.
   (https://viblo.asia/p/tim-hieu-ve-thu-vien-sigar-XL6lARoD5ek)
  
+ How-to-install-sigar-on-ubuntu-based-linux
```bazaar
wget https://netcologne.dl.sourceforge.net/project/sigar/sigar/1.6/hyperic-sigar-1.6.4.tar.gz
tar xvf hyperic-sigar-1.6.4.tar.gz
cd hyperic-sigar-1.6.4

# INSTALL
sudo cp sigar-bin/lib/libsigar-`dpkg --print-architecture`-`uname -s | tr '[:upper:]' '[:lower:]'`.so /usr/lib

```