# NBN Monitoring
## Introduction

Application for the monitoring of Internet/WAN connections checking the connection is up and running and the internet can be accessed.

## Solution

The hardware being used for this an old Raspberry Pi I have lying about. Connected directly to the Telstra Smart Modem using Ethernet.

![Hardware](doco/hardwareSetup.png)

The software side of the application is broken down into three components. The first performs monitoring of the internet connection by performing TCP traceroute and ping tests, and grabs the status from the modem. This monitoring is written to a flat file. The second component reads and parses the monitoring date from the flat file and saves to a MongoDb database. The third component are the screens, a J2EE WAR with a couple of screens accessing the monitoring results from the database.

## Raspberry Pi Configuration

Steps taken to configure the PI server.

#### 1) Updates

Make sure that we are fully up to date.
```Bash
$ sudo apt update
$ sudo apt full-upgrade
$ sudo apt autoremove
$ sudo apt clean
```


#### 2) Configure hostname  

Configured a known host name for easier access on the private network. Need to add the name to hostname file, plus add a hostename reference in the hosts file;
```Bash
$ sudo vi /etc/hostname
ws1
$ sudo vi /etc/hosts
127.0.1.1	ws1
```

#### 3) Enable remote connections

Not critical for the application, just makes life easier.

From main menu->Preferences->Raspberry Pi Configuration

In the configuration dialog select then Interfaces tab, then enable both SSH and VNC.

#### 4) Change PI default screen resolution

Advanced Options > Resolution, and choose an option.
'''
hdmi_mode=85
'''

#### 5) Install and Configure Samba

Samba is used for exposing a network drive that is used for transferring files.

Useful doco to read: <https://www.raspberrypi.org/documentation/remote-access/samba.md>

```Bash
$ sudo apt install samba samba-common-bin
$ sudo apt install samba samba-common-bin smbclient cifs-utils
```

First configure the netbios name.

```Bash
$ sudo vi /etc/samba/smb.conf
netbios name = ws1
```

Next add a mount point (at the end of the file) that can be used for uploading and downloading files.

```Bash
$ mkdir -p /home/pi/monitorNbn/share
$ sudo vi /etc/samba/smb.conf
[monitorNbnShare]
   path = /home/pi/monitorNbn/share
   browseable = yes
   guest ok = yes
   guest account = pi
   read only = no
   create mask = 0777
   directory mask = 0777
   public = yes
   writeable = yes
```

Restart the service to pick up the new configuration.

```Bash
sudo systemctl restart smbd
```

#### 6) Downgrade to Java8

The default is Java9 is installed, this does not run on on my model of PI. When attempting to run the the following error message is displayed "Server VM is only supported on ARMv7+ VFP"

```Bash
$ sudo apt-get remove openjdk*
$ sudo apt-get autoremove
$ tar -xvf jdk-8u231-linux-arm32-vfp-hflt.tar.gz
$ sudo update-alternatives --install /usr/bin/java java /home/pi/Apps/java/jdk1.8.0_231/bin/java 1
$ sudo update-alternatives --set java /home/pi/Apps/java/jdk1.8.0_231/bin/java
$ vi ~/.profile
export JAVA_HOME="/home/pi/Apps/java/jdk1.8.0_231"
export PATH="$JAVA_HOME/bin:$PATH"
$ sudo update-alternatives --install /usr/bin/java java /home/pi/Apps/java/jdk1.8.0_231/bin/java 1
$ sudo update-alternatives --set java /home/pi/Apps/java/jdk1.8.0_231/bin/java
```

And test the update is successful.

```Bash
$ java -version
java version "1.8.0_231"
Java(TM) SE Runtime Environment (build 1.8.0_231-b11)
Java HotSpot(TM) Client VM (build 25.231-b11, mixed mode)
```

#### 7) Install MongoDb

```Bash
$ sudo apt-get install mongodb
$ service mongodb status
$ sudo vi /etc/mongodb.conf
Update the bind ip to 0.0.0.0, e.g.
bind_ip = 0.0.0.0
#bind_ip = 127.0.0.1
#port = 27017
$ sudo service mongodb restart
```

#### 8) Install Tomcat

```Bash
$ sudo apt-get install tomcat8
```

Make the webapps directory writeable ready for deployments.

```Bash
$ sudo chmod a+w /var/lib/tomcat8/webapps
```

## Configure Development Environment

#### 1) Software

Install the following applications;
* Virtual Box
* Vagrant

#### 2) Code

Checkout code from <https://github.com/whamBamDev/monitorNbn.git> to under a local folder, e.g. `D:\Dev\monitorNbn` (the code cloned out to folder `D:\Dev\monitorNbn\monitorNbn`.

#### 3) Vagrant Setup

Install vagrant plugin vbguest

```Bash
$ cd D:\Dev\monitorNbn\monitorNbn\vagrantDevBox
$ vagrant plugin install vagrant-vbguest
```

Under D:\Dev\monitorNbn\monitorNbn\vagrantDevBox copy file localConfig.rb.example to localConfig.rb.
Edit and update the directory parameter to development area.
```
HOST_DEV_DIR = 'D:/Dev/monitorNbn'
```

#### 4) New Folders

Create the following folders;
* `D:\Dev\monitorNbn\installDownload`
* `D:\Dev\monitorNbn\share`

#### 5) Java install
Download Oracle JDK version 8u241 (Linux x64 RPM - filebname jdk-8u241-linux-x64.rpm) and put under 
D:\Dev\monitorNbn\installDownload. Unfortunatly it is no longer possible to download Java8 using from within a vagrant script (was done using wget).

#### 6) Start VM

Then create and start the VM
```Bash
$ vagrant up
```

#### 7) Building

[Gradle](https://gradle.org/) is the tool used for building, to build all the modules in the project the issue a 'build' task from the master folder.

```Bash
$ cd master
$ gradle clean build
```

For developing the UI (the nbnonitorWar module) the application can be run as a Spring Boot application or deployed under Tomcat. For development then it is quickest to use Spring Boot as running with DevTools. Tomcat is used as a final test before deploying onto the Rapsberry Pi.

So to run under Spring Boot then pen two command line windows. In the first window run Spring Boot.

```Bash
$ cd nbnMonitorWar
$ gradle -x test bootRun
```

In the second window then run a continuous build, this will auto deploy changes allowing for instant testing.

```Bash
$ cd nbnMonitorWar
$ gradle assemble --continuous
```

Then the UI can be accessed via url <http://localhost:8080>

Another couple of useful urls are <http://localhost:8080/api/dailySummary> and <http://localhost:8080/api/outage?date=2020-07-04>, they are examples of accessing the API used by the UI

Cargo is used for deploying under Tomcat. To run then submit the following. 

```Bash
$ cd nbnMonitorWar
$ gradle cargoStartLocal
$ tail -100f build/output.log
```

Then the ui can be accessed via url <http://localhost:8080/nbnMonitor/>

To re-deploy the issue the following command.

```Bash
$ gradle cargoRedeployLocal
```


## Deployment on Raspberry Pi

#### 1) Upload Code

Login into the PI and create the following folders;
* `/home/pi/monitorNbn/share`
* `/home/pi/monitorNbn/share/output`

To start then upload the code that tests the connection, plus code that save the data to the database. The
instructions for UI are later on.

The first artefacts to deploy is `modemStatus-001.zip`, upload to `/home/pi/monitorNbn/share` and extract  to `/home/pi/monitorNbn/share/modemStatus-001`. Also upload the file `NbnConnection.sh` to `/home/pi/monitorNbn/share.

The first artefact to deploy is `dataLoader-1.0.zip`, upload to `/home/pi/monitorNbn/share`and extract  to `/home/pi/monitorNbn/share/dataLoader`.

#### 2) Cron Jobs

To start monitoring and saving the data to the database then add the following three cron jobs.

The first job runs every minute, it tests the connection and the results are written to a dated log file in folder `/home/pi/monitorNbn/share/output`.

The second job runs every day a 4am, it tails the days data file (in folder `/home/pi/monitorNbn/share/output`) and adds the results to the database as soon as the are written.

The third job runs every day a 4am, it loads data from log  files in folder `/home/pi/monitorNbn/share/output` to the database. This job loads data from the previous day, it's a backup for the 2nd job just in case it fails during the day.

```Bash
$ crontab -e

NBN_HOME=/home/pi/monitorNbn/share
NBN_DATA=/home/pi/monitorNbn/share/output
NBN_DATA_LOADER=/home/pi/monitorNbn/share/dataLoader
#
# m h  dom mon dow   command
* * * * * flock -w 10 ${NBN_DATA}/modemStatus.lock ${NBN_HOME}/monitorNbnConnection.sh >> ${NBN_DATA}/modemStatus_`date +\%Y\%m\%d`.dat 2>&1
5 0 * * * ${NBN_DATA_LOADER}/bin/dataLoader -t -f ${NBN_DATA}/modemStatus_`date +\%Y\%m\%d`.dat >> ${NBN_DATA_LOADER}/log/dataLoaderTail.log 2>&1
0 4 * * * ${NBN_DATA_LOADER}/bin/dataLoader -f ${NBN_DATA}/modemStatus_`date --date yesterday "+\%Y\%m\%d"`.dat >> ${NBN_DATA_LOADER}/log/dataLoader.log 2>&1
```

#### 3) Monitoring Verification

Once the jobs have been created then check folder `/home/pi/monitorNbn/share/output` that the log files are being created.

The jobs will load the results into the MongoDb database. If required the data can be load manually too. To load for a single day then enter the following command (note change the date to the date you require).

```Bash
$ ./dataLoader -f /home/pi/monitorNbn/share/output/modemStatus_20200401.dat
```

Also data from all the files can be reloaded with the following.

```Bash
find  /home/pi/monitorNbn/share/output -not \( -path **/backup/* -prune \) -name "modemStatus_*.dat" -exec ./dataLoader -f  {} \;
```

Also check the database is being populated, here are a few example commands to query the database.

```JavaScript
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
```

#### 4) Deploy application

Finally the deployment of the UI. This is being run under the root context of Tomcat. Upload the `nbnMonitorWar-1.0.war` artefact to `/home/pi/monitorNbn/share` and copy to the Tomcat webapps folder.

```Bash
$ cp /home/pi/monitorNbn/share/nbnMonitorWar-1.0.war /var/lib/tomcat8/webapps/ROOT.war
```

The very first the war is deployed the remove the ROOT context that was part of the Tomcat installation. This does not need to be done on subsequent deployments.

```Bash
sudo rm -Rf /var/lib/tomcat8/webapps/ROOT
```

Then watch the log file for the deployment to complete.

```Bash
$ tail -f /var/log/tomcat8/catalina.out
```

Once deployed the application can be accessed via <http://ws1:8080>.

