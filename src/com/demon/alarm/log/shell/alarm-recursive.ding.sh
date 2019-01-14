#!/bin/bash
# author xuliang
# since 2019-1-8

# EMAILS="1181486095@qq.com"
LOG_HOME=/data/platform/logs
# ALARM_CONTEXT=~/alarm_context

# ALARM_SHELL_CONTEXT=~/svn/java/docs/shell/alarm
ALARM_SHELL_CONTEXT=/data/platform/shell/alarm

# TEST
# 0: alarming closed
# 1: specific alarming
# 2: all alarming open

if [ -x "alarm-config.sh" ]; then
    . alarm-config.sh
fi

if [ "$TEST" == "0" ]; then
    echo "alarm-config TEST:$TEST, email alarming is closed!!!"
    exit
fi

echo "TEST=$TEST EMAILS=$EMAILS DING=$DING"

cd $LOG_HOME && pwd

for DIR in `find . -maxdepth 2 -path "*.com*" -type d | sed -e '/^alarm/d' -e '/\/resources$/d'`
do
    echo "DIR: $DIR"
    if [ "$TEST" == "1" ] && [ "${DIR##*/}" != "yun.uuuwin.com" ]; then
        echo "Test: ignore handle $DIR"
        continue
    fi

    for FILE in `find $DIR -maxdepth 1 -regex ".+_[(error)|(warn)]+\.log" | sort`
    do
        echo "FILE: $FILE"
        LOG_CONTEXT=$LOG_HOME/${DIR#*/}
        LOG_FILE=${FILE##*/}
        echo "LOG_CONTEXT=$LOG_CONTEXT LOG_FILE=$LOG_FILE"
        #bash -x $ALARM_SHELL_CONTEXT/alarm-1.0.sh --emails $EMAILS --log-context $LOG_CONTEXT --file $LOG_FILE
	    bash -x $ALARM_SHELL_CONTEXT/alarm-1.1.sh --mode ding --dingding $DING --log-context $LOG_CONTEXT --file $LOG_FILE
        break
    done
done

