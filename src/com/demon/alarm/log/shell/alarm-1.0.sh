#!/bin/bash
# author xuliang
# since 2018-07-12
# 分析日志，若日志有变化，则发送到邮箱中
# 1、本地记录日志对应的日期版本，日志的行数
# 2、若日期为当天，则比较当前日志的行数和之前记录的行数是否一致，不一致，则将之前行数到现在行数新增的日志内容，发送到相应的邮箱里
# 3、若日期不为当天，则修改日期版本为当天，日志行数为0

ALARM_CONTEXT=/root/alarm/config

while [ ".$1" != . ]
do
  case "$1" in
    --emails )
      EMAILS="$2"
      shift; shift;
      continue
    ;;
    --log-context )
      LOG_CONTEXT="$2"
      shift; shift;
      continue
    ;;
    --log-file )
      LOG_FILE="$2"
      shift; shift;
      continue
    ;;
    --alarm-context )
      ALARM_CONTEXT="$2"
      shift; shift;
      continue
    ;;
    * )
      break
    ;;
  esac
done

echo "ALARM_CONTEXT=$ALARM_CONTEXT EMAILS=$EMAILS LOG_CONTEXT=$LOG_CONTEXT LOG_FILE=$LOG_FILE"

test ".$EMAILS" == . && echo "EMAILS not found!" && exit
test ".$LOG_CONTEXT" == . && echo "LOG_CONTEXT not found!" && exit
test ".$LOG_FILE" == . && echo "LOG_FILE not found!" && exit

# constants
FILE_LOG=$LOG_CONTEXT/$LOG_FILE
FILE_ALARM_INDEX=$ALARM_CONTEXT/$LOG_FILE.index
FILE_ALARM_VERSION=$ALARM_CONTEXT/$LOG_FILE.version

# 日志文件不存在不处理
if [ ! -f "$FILE_LOG" ]; then
  echo "$FILE_LOG not found!"
  exit
fi

# 监控目录不存在的话，新建
if [ ! -d "$ALARM_CONTEXT" ]; then
  mkdir -p $ALARM_CONTEXT
  # $? 表示上一个命令的返回值，test -ne 不等于
  test "$?" -ne 0 && echo "mkdir $ALARM_CONTEXT fail!" && exit
fi

# 监控日志对应的索引值文件不存在的话，新建
if [ ! -f "$FILE_ALARM_INDEX" ]; then
  echo "create file: $FILE_ALARM_INDEX"
  echo "0" > $FILE_ALARM_INDEX
fi

TODAY=`date +%Y-%m-%d`
# 校验日志文件最后修改时间是否为今天
# stat参数：-c 自定义输出格式 %y 最后修改时间
# cut参数：-c 显示行中指定范围的字符
if [ "$TODAY" != "`stat -c %y $FILE_LOG | cut -c 1-10`" ]; then
  echo "$FILE_LOG last modify isn't today"
  stat $FILE_LOG
  exit
fi

# 监控日志的版本文件不存在的话，新建
if [ ! -f "$FILE_ALARM_VERSION" ]; then
  echo "create file: $FILE_ALARM_VERSION"
  echo "0" > $FILE_ALARM_VERSION
fi

# 监控日志的版本文件最后修改日期不是今天时，重置版本信息
if [ "$TODAY" != "`stat -c %y $FILE_ALARM_VERSION | cut -c 1-10`" ]; then
  echo "$FILE_ALARM_VERSION is expired. reset to 0"
  echo "0" > $FILE_ALARM_VERSION
fi

OLD_LINE=`cat $FILE_ALARM_INDEX`
NEW_LINE=`cat $FILE_LOG | wc -l`

VERSION=`date +%Y%m%d`
OLD_VERSION=`cat $FILE_ALARM_VERSION`

# 若旧的版本号不为当天的版本号，或者旧的行数大于当前日志行数，那么认为是新的一天的日志，将旧的行数置为0
if [ "$VERSION" -ne "$OLD_VERSION" -o "$OLD_LINE" -gt "$NEW_LINE" ]; then
  OLD_LINE=0
fi

echo "OLD_LINE=$OLD_LINE NEW_LINE=$NEW_LINE VERSION=$VERSION OLD_VERSION=$OLD_VERSION"
if [ "$OLD_LINE" == "$NEW_LINE" ]; then
  echo "FILE_LOG is same as last time in check: $FILE_LOG"
fi

if [ "$OLD_LINE" -ne "$NEW_LINE" ]; then
  START_LINE=`expr $OLD_LINE + 1`
  END_LINE=$NEW_LINE
  FILE_ALARM=$ALARM_CONTEXT/$LOG_FILE.${VERSION}.${START_LINE}_${END_LINE}.txt

  echo "sed ${FILE_LOG} from line ${START_LINE} to ${END_LINE}"
  # 截取变动的日志内容，放入待发送文本里
  sed -n "${START_LINE},${END_LINE}p" ${FILE_LOG} > ${FILE_ALARM}

  echo $NEW_LINE > $FILE_ALARM_INDEX
  echo $VERSION > $FILE_ALARM_VERSION
  echo "Refresh NEW_LINE=`cat $FILE_ALARM_INDEX` VERSION=`cat $FILE_ALARM_VERSION`"
  
  # mutt 邮件发送
  # -s 邮件标题 ALARM: 日志文件名
  # -b 邮件接收人
  # 邮件正文是待发送文件的前20行
  # -a 邮件附件，待发送文件
  head -20 ${FILE_ALARM} | mutt -s "ALARM: ${LOG_FILE}" -b ${EMAILS} -a ${FILE_ALARM}
  echo "mutt alarm file: $FILE_ALARM"
fi
