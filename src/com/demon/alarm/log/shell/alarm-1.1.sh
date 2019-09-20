#!/bin/bash
# author xuliang
# since 2018-07-12
# 分析日志，若日志有变化，则发送到邮箱中
# 1、本地记录日志对应的日期版本，日志的行数
# 2、若日期为当天，则比较当前日志的行数和之前记录的行数是否一致，不一致，则将之前行数到现在行数新增的日志内容，发送到相应的邮箱里
# 3、若日期不为当天，则修改日期版本为当天，日志行数为0

# modify by xuliang 2019-1-8
# 增加预警方式，参数“--mode”，mode=email时，“--emails”参数必输，为邮件地址；mode=ding时，“--dingding”参数必输，为钉钉机器人webhook

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
		--alarm-context )
			ALARM_CONTEXT="$2"
			shift; shift;
			continue
		;;
		--file )
			FILE="$2"
			shift; shift;
			continue
		;;
		--mode )
		      ALARM_MODE="$2"
		      shift;shift;
		      continue
		;;
		--dingding )
		      ALARM_DING="$2"
		      shift;shift;
		      continue
		;;
		--prefix )
              LOG_PREFIX="$2"
              shift;shift;
              continue
        ;;
		* )
			break
		;;
	esac
done

echo "left params: $*"
echo "left params: 1=$1 2=$2 3=$3"

if [ -z "$ALARM_MODE" ]; then
  ALARM_MODE="email"
fi

if [[ "$ALARM_MODE" != "email" && "$ALARM_MODE" != "ding" ]]; then
  ALARM_MODE="email"
fi

test ".$ALARM_MODE" == .email -a ".$EMAILS" = . && echo "EMAILS not found!" && exit
test ".$ALARM_MODE" == .ding -a ".$ALARM_DING" == . && echo "DING not found!" && exit
test ".$LOG_CONTEXT" = . && echo "LOG_CONTEXT not found!" && exit
test ".$FILE" = . && echo "FILE not found!" && exit

# constants
FILE_LOG=$LOG_CONTEXT/$FILE

if [ $LOG_PREFIX == "" ]; then
  FILE_ALARM_INDEX=$ALARM_CONTEXT/$FILE.index
  FILE_ALARM_VERSION=$ALARM_CONTEXT/$FILE.version
  FILE_ALARM_MESSAGE=$ALARM_CONTEXT/$FILE.message
else
  FILE_ALARM_INDEX=$ALARM_CONTEXT/${LOG_PREFIX}_$FILE.index
  FILE_ALARM_VERSION=$ALARM_CONTEXT/${LOG_PREFIX}_$FILE.version
  FILE_ALARM_MESSAGE=$ALARM_CONTEXT/${LOG_PREFIX}_$FILE.message
fi

if [ ! -f "$FILE_LOG" ]; then
	echo "$FILE_LOG not found!"
	exit
fi

if [ ! -d "$ALARM_CONTEXT" ]; then
	mkdir -p $ALARM_CONTEXT
	test "$?" -ne 0 && echo "mkdir $ALARM_CONTEXT fail!" && exit
	chown alarm:alarm -R $ALARM_CONTEXT
fi

if [ ! -f "$FILE_ALARM_INDEX" ]; then
    echo "create file: $FILE_ALARM_INDEX"
	echo "0" > $FILE_ALARM_INDEX
fi

# check FILE_LOG last modify time, ignore process if not today
TODAY=`date +%Y-%m-%d`
if [ "$TODAY" != "`stat -c %y $FILE_LOG | cut -c 1-10`" ]; then
    echo "$FILE_LOG last modify time isn't today:"
    stat $FILE_LOG
    exit
fi

# check FILE_ALARM_VERSION, set default value 0 if file not found
if [ ! -f "$FILE_ALARM_VERSION" ]; then
    echo "create file: $FILE_ALARM_VERSION"
	echo "0" > $FILE_ALARM_VERSION
fi

# check FILE_ALARM_VERSION last modify time, reset to 0 if not today
if [ "$TODAY" != "`stat -c %y $FILE_ALARM_VERSION | cut -c 1-10`" ]; then
    echo "$FILE_ALARM_VERSION is expired, reset to 0"
    echo "0" > $FILE_ALARM_VERSION
fi

OLD_LINE=`cat $FILE_ALARM_INDEX`
NEW_LINE=`cat $FILE_LOG | wc -l`

VERSION=`date +%Y%m%d`
OLD_VERSION=`cat $FILE_ALARM_VERSION`

if [ "$OLD_VERSION" -ne "$VERSION" -o "$OLD_LINE" -gt "$NEW_LINE" ]; then
	OLD_LINE=0
fi

echo "OLD_LINE=$OLD_LINE NEW_LINE=$NEW_LINE"
if [ "$OLD_LINE" == "$NEW_LINE" ]; then
    echo "FILE_LOG is the same as last time in check: $FILE_LOG"
fi

if [ "$OLD_LINE" -ne "$NEW_LINE" ]; then
	START_LINE=`expr $OLD_LINE + 1`
	END_LINE=$NEW_LINE
	FILE_ALARM=$ALARM_CONTEXT/$FILE.${VERSION}.${START_LINE}_${END_LINE}.txt

	echo "sed log file from line ${START_LINE} to ${END_LINE}"
	sed -n "${START_LINE},${END_LINE}p" $FILE_LOG > $FILE_ALARM

	echo $NEW_LINE > ${FILE_ALARM_INDEX}
	echo "$VERSION" > ${FILE_ALARM_VERSION}

	echo "Refresh NEW_LINE=`cat ${FILE_ALARM_INDEX}`"
	echo "Refresh NEW_VERSION=`cat ${FILE_ALARM_VERSION}`"
	
	head -n 1 $FILE_ALARM > ${FILE_ALARM_MESSAGE}

	if [ "$ALARM_MODE" == "email" ]; then
		# ifconfig >> ${FILE_ALARM_MESSAGE}
		mutt -s "ALARMING: $FILE _ALARM" -b $EMAILS -a $FILE_ALARM < ${FILE_ALARM_MESSAGE}
		echo "mutt alarm file: $FILE_ALARM"
		echo "mutt alarm file message: $FILE_ALARM_MESSAGE"
	elif [ "$ALARM_MODE" == "ding" ]; then
		 # 调用钉钉机器人发送报警信息
		 IP=`/sbin/ifconfig eth1|grep "inet "|awk '{print $2}'|awk '{print $1}'`
		 #echo $IP
		 curl "$ALARM_DING" \
		    -H "Content-Type: application/json" \
		    -d "{\"msgtype\": \"markdown\", \
		         \"markdown\": {\"title\":\"ALARM: ${FILE}\",\"text\":\"**${IP} ALARM: ${FILE}** \\n `head -5 ${FILE_ALARM}`\"}, \
		         \"at\": {\"atMobiles\": [],\"isAtAll\": false}}"
		 echo "ding alarm file: $FILE_ALARM"
	else
		 echo "MODE($ALARM_MODE) not support."
	fi
fi

