#!/bin/bash
# author xuliang
# since 2018-07-10
# 查询日志文件中的error和warn日志，调用shell分析日志，日志有变化则发送到邮箱中，用以监控程序问题

# 收件人
EMAILS="115449424@qq.com"
# 日志目录
LOG_HOME=/root/alarm
# 程序目录
ALARM_SHELL_CONTEXT=/root/alarm/shell

echo "TEST=$TEST EMAILS=$EMAILS"

cd $LOG_HOME && pwd

# find参数：-maxdepth 2 最大目录层级为2；-path "*.com*" 查找名称中包含.com的文件；-type d 查找目录
# sed参数：-e <script> 以选项中指定的script来处理输入的文本文件；'/^alarm/d' ^ 匹配行开始，指匹配以alarm开头的行，d 指删除选择的行；'/\/resources$/d' $ 匹配行结束，指删除以/resources结尾的行
for DIR in `find . -maxdepth 2 -path "*.com*" -type d | sed -e '/^alarm/d' -e '/\/resources$/d'`
do
  echo "DIR: $DIR"
  
  # ${DIR##*/} 拿掉最后一条 / 及其左边的字符串
  # eg. DIR=./log/pk.uuuwin.com, ${DIR##*/} 结果为 pk.uuuwin.com
  if [ "$TEST" == "1" ] && [ "${DIR##*/}" != "pk.uuuwin.com" ]; then
    echo "Test: ignore handle $DIR"
    continue
  fi
  
  for FILE in `find $DIR -maxdepth 1 -regex ".+[(error)|(warn)\.log]" | sort`
  do
    echo "FILE: $FILE"
    
    # ${FILE#*/} 拿掉第一条 / 及其左边的字符串
    LOG_CONTEXT=$LOG_HOME/${DIR#*/}
    LOG_FILE=${FILE##*/}
    echo "LOG_CONTEXT=$LOG_CONTEXT LOG_FILE=$LOG_FILE"
    bash -x $ALARM_SHELL_CONTEXT/alarm-1.0.sh --emails $EMAILS --log-context $LOG_CONTEXT --log-file $LOG_FILE
    break
  done
done
