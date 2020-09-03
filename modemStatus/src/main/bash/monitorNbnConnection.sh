#!/bin/bash
time_now=$(date +"%d-%m-%Y %T") 
echo "==== start(001): ${time_now}"

time_now=$(date +"%d-%m-%Y %T")
echo "== traceroute - IP: ${time_now}"
echo "tcptraceroute 203.50.5.178 80 -w 1"
sudo tcptraceroute 203.50.5.178 80 -w 1
echo "Exit Code: $?"
echo ""

time_now=$(date +"%d-%m-%Y %T")
echo "== traceroute - DNS Lookup: ${time_now}"
echo "tcptraceroute www.telstra.com.au 80 -w 1"
sudo tcptraceroute www.telstra.com.au 80 -w 1
echo "Exit Code: $?"
echo ""

time_now=$(date +"%d-%m-%Y %T")
echo "== ping - DNS Lookup: ${time_now}"
echo "ping www.telstra.com.au -4 -c 1"
ping www.telstra.com.au -4 -c 1
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
