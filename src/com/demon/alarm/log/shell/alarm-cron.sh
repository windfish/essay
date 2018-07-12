#!/bin/bash
# author xuliang
# since 2018-07-12

ALARM_OUT=/root/alarm/log/alarm.out

cd /root/alarm/shell && pwd

bash -x alarm-entrance.sh | tee $ALARM_OUT
