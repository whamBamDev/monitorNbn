#!/bin/bash
echo ==========start
date

echo ==traceroute
echo "tcptraceroute www.telstra.com.au 80 -w 1 -m 10 -S"
tcptraceroute www.telstra.com.au 80 -w 1 -m 10 -S
echo "Exit Code: $?

echo ==ping
echo "ping www.telstra.com.au -c 1"
ping www.telstra.com.au -c 1
echo "Exit Code: $?

echo ==========end
