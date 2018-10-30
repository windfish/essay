#!/bin/bash
# author xuliang
# since 2018-10-30

procname=$1

if [ -z $procname ]; then
    ps -ef --cols `tput cols` --sort cmd
else
    ps -ef --cols `tput cols` --sort cmd | grep $procname
fi
