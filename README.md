

To configure the PI

Before starting then : $ sudo apt update
$ sudo apt full-upgrade
$ sudo apt autoremove
$ sudo apt clean
https://www.raspberrypi.org/documentation/raspbian/updating.md


1) Configue hostname
   $ sudo vi /etc/hostname
   $ sudo vi /etc/hosts

2) Enable remote connections

From main menu->Preferences->Raspberry Pi Configuration

In the configuration dialog select then Interfaces tab, then enable both SSH and VNC.

3) Chanhge PI default screen resolution

 Advanced Options > Resolution, and choose an option.

hdmi_mode=85


2) Samba

First read: https://www.raspberrypi.org/documentation/remote-access/samba.md

sudo apt install samba samba-common-bin

sudo apt install samba samba-common-bin smbclient cifs-utils


sudo systemctl restart smbd

3) Downgrade to Java8
The default is Java9 which does not run on on my model of PI, when attempting to run the the
following error message is displayed "Server VM is only supported on ARMv7+ VFP"


$ sudo apt-get remove openjdk*
$ sudo apt-get autoremove
$ tar -xvf jdk-8u231-linux-arm32-vfp-hflt.tar.gz

$ sudo update-alternatives --install /usr/bin/java java /home/pi/Apps/java/jdk1.8.0_231/bin/java 1
$ sudo update-alternatives --set java /home/pi/Apps/java/jdk1.8.0_231/bin/java

export JAVA_HOME="/home/pi/Apps/java/jdk1.8.0_231"
export PATH="$JAVA_HOME/bin:$PATH"


$ sudo update-alternatives --install /usr/bin/java java /home/pi/Apps/java/jdk1.8.0_231/bin/java 1
$ sudo update-alternatives --set java /home/pi/Apps/java/jdk1.8.0_231/bin/java


jdk1.8.0_231


4) Install MongoDb

$ sudo apt-get install mongodb

$ service mongodb status

$ sudo vi /etc/mongodb.conf

Update the bind ip to 0.0.0.0, e.g.
bind_ip = 0.0.0.0
#bind_ip = 127.0.0.1
#port = 27017


$ sudo service mongodb restart
Useful commands;

> show dbs
> use nbn
> show collections
> db.DailySummary.find()
{ "_id" : ObjectId("5ee4dcb4f95e1856923e805c"), "datafile" : "/home/pi/monitorNbn/share/output/modemStatus_20200401.dat", "date" : ISODate("2020-04-01T00:00:00Z"), "outageCount" : 8, "testCount" : 1438 }
> 

> db.DailySummary.find( { date: { $gt: "2020-06-15" } } ).pretty()
> db.DailySummary.find( { date: { $gt: "2020-06-15" } } ).sort( { date: 1 })
> db.DailySummary.find( { testCount: { $lt: 1000 } } ).sort( { date: 1 })

> db.Outage.find( { startTime: { $gte: "2020-06-15", $lt: "2020-06-16"} } ).sort( { startTime: 1 })

"startTime" : "2020-06-13T08:35:01"


$ ./dataLoader -f /home/pi/monitorNbn/share/output/modemStatus_20200401.dat

//Reload whole 
find  /home/pi/monitorNbn/share/output -not \( -path **/backup/* -prune \) -name "modemStatus_*.dat" -exec ./dataLoader -f  {} \;



x) Tomcat

$ sudo apt-get install tomcat9

Make the webapps directory writeable
sudo chmod a+w /var/lib/tomcat8/webapps

Deploy application

$ cp /home/pi/monitorNbn/share/nbnMonitorWar-1.0.war /var/lib/tomcat8/webapps

tail -f /var/log/tomcat8/catalina.out

http://localhost:8080/nbnMonitor
http://localhost:8080/nbnMonitor/api/dailySummary
http://localhost:8080/nbnMonitor/api/outage?date=2020-07-04

http://ws1:8080/nbnMonitorWar-1.0/

7) 

$ cromtab -e

NBN_HOME=/home/pi/monitorNbn/share
NBN_DATA=/home/pi/monitorNbn/share/output
NBN_DATA_LOADER=/home/pi/monitorNbn/share/output

* * * * * flock -w 10 ${NBN_DATA}/modemStatus.lock ${NBN_HOME}/monitorNbnConnection.sh >> ${NBN_DATA}/modemStatus_`date +\%Y\%m\%d`.dat 2>&1

0 6 * * * ${NBN_DATA_LOADER}/bin/dataLoader -f ${NBN_DATA}/modemStatus_`date --date yesterday "+\%Y\%m\%d"`.dat >> ${NBN_DATA_LOADER}/log/dataLoader.log 2>&1


/home/pi/monitorNbn/share/dataLoader/bin/dataLoader -f /home/pi/monitorNbn/share/output/modemStatus_`date --date yesterday "+\%Y\%m\%d"`.dat >> /home/pi/monitorNbn/share/dataLoader/dataLoader.log 2>&1

${NBN_DATA_LOADER}/bin/dataLoader -f ${NBN_DATA}/modemStatus_`date --date yesterday "+\%Y\%m\%d"`.dat >> ${NBN_DATA_LOADER}/log/dataLoader.log 2>&1

dt=$(date --date yesterday "+%a %d/%m/%Y")

$ ./dataLoader -f /home/pi/monitorNbn/share/output/modemStatus_20200401.dat

Configure Develpoment Environment

1) Install the following;
Virtual Box
Vagrant

2) checkout code from https://github.com/whamBamDev/monitorNbn.git to under a local folder, e.g. D:\Dev\monitorNbn (the code cloned out to folder "D:\Dev\monitorNbn\monitorNbn".

3) Install vagrant plugin vbguest 
$ vagrant plugin install vagrant-vbguest

4) Under D:\Dev\monitorNbn\monitorNbn\vagrantDevBox copy file localConfig.rb.example to localConfig.rb.
Edit and update the directory parameter to development area.
   HOST_DEV_DIR = 'D:/Dev/monitorNbn'

5) Create the following folders;
D:\Dev\monitorNbn\installDownload
D:\Dev\monitorNbn\share

6) Download Oracle JDK version 8u241 (Linux x64 RPM - filebname jdk-8u241-linux-x64.rpm) and put under 
D:\Dev\monitorNbn\installDownload. Unfortunatly it is no longer possible to download Java8 using from within a vagrant script 
(using wget).

7) Then create the VM
vagrant up





Server at localhost:27017 reports wire version 0, but this version of the driver requires at least 2 (MongoDB 2.6).


Build

$ gradle -x test bootRun

$ gradle -x test assemble --continuous
