#!/bin/bash
augtool <<-EOF
print /files/etc/gdm/custom.conf
set /files/etc/gdm/custom.conf/daemon/AutomaticLoginEnable "true"
set /files/etc/gdm/custom.conf/daemon/AutomaticLogin "vagrant"
save
EOF
