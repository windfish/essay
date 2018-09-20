#!/bin/bash

procname=$1

if [ -z $procname ]; then
    ps -ef --cols `tput cols` --sort cmd
else
    ps -ef --cols `tput cols` --sort cmd | grep $procname
fi

