#!/bin/bash
time_now=$(date +"%d-%m-%Y %T") 
echo "==== start(001): ${time_now}"
time_now=$(date +"%d-%m-%Y %T")
echo "== traceroute - direct IP: ${time_now}"
echo "tcptraceroute 203.36.190.11 80 -w 1 -m 10 -S"
sudo tcptraceroute 203.36.190.11 80 -w 1 -m 10 -S
echo "Exit Code:" $?
echo ""

time_now=$(date +"%d-%m-%Y %T")
echo "== traceroute - DNS Lookup: ${time_now}"
echo "tcptraceroute www.telstra.com.au 80 -w 1 -m 10 -S"
sudo tcptraceroute www.telstra.com.au 80 -w 1 -m 10 -S
echo "Exit Code: $?"
echo ""

time_now=$(date +"%d-%m-%Y %T")
echo "== ping - direct IP: ${time_now}"
echo "ping 203.36.190.11 -c 1"
ping 203.36.190.11 -c 1
echo "Exit Code: $?"
echo ""

time_now=$(date +"%d-%m-%Y %T")
echo "== ping - DNS Lookup: ${time_now}"
echo "ping www.telstra.com.au -c 1"
ping www.telstra.com.au -c 1
echo "Exit Code: $?"
echo ""

time_now=$(date +"%d-%m-%Y %T")
echo "== Modem Status: ${time_now}"
/home/pi/monitorNbn/share/modemStatus-001/bin/modemStatus
echo "Exit Code: $?"
echo ""
time_now=$(date +"%d-%m-%Y %T")
echo "==== end: ${time_now}"
