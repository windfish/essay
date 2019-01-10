#!/bin/bash
# author xuliang
# since 2019-1-8
# example:
# su alarm
# ./alarm-cron-ding.sh

ALARM_OUT=/data/platform/logs/alarm.uuuwin.com/alarm.out

cd /data/platform/shell/alarm/ && pwd

bash -x alarm-recursive.ding.sh | tee $ALARM_OUT
