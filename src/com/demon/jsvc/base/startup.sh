#!/bin/bash

JSVC_HOME=/root/server/jsvc_home
JSVC=$JSVC_HOME/jsvc
JAVA_OPTS=
if [ -r "$JSVC_HOME/setenv.sh" ]; then
  . "$JSVC_HOME/setenv.sh"
fi

BASE_HOME=/root/server/chatroom_server
CLASSPATH=
for jar in $BASE_HOME/lib/*.jar
do
        CLASSPATH=$CLASSPATH:$jar
done

CLASSPATH="$CLASSPATH:$BASE_HOME/classes:$JSVC_HOME/commons-daemon-1.1.0.jar"

echo "CLASSPATH=$CLASSPATH"

JMAIN_OUT=$BASE_HOME/jmain-daemon.out
JMAIN_PID=$BASE_HOME/jmain-daemon.pid

MAIN=com.demon.netty.chapter11.chatroom.ChatRoomServer


case "$1" in
    start   )
      $JSVC -procname "chatroom" \
      -pidfile "$JMAIN_PID" \
      -java-home "$JAVA_HOME" \
      -wait "10" \
      -errfile "$JMAIN_OUT" \
      -classpath "$CLASSPATH" \
      $JAVA_OPTS \
      $MAIN
      exit $?
    ;;
    stop    )
      $JSVC -stop \
      -procname "chatroom" \
      -pidfile "$JMAIN_PID" \
      -classpath "$CLASSPATH" \
      $MAIN
      exit $?
    ;;
    *       )
      echo "Unknown command: \`$1'"
      echo "Usage: $PROGRAM ( commands ... )"
      echo "commands:"
      echo "  start             Start Tomcat"
      echo "  stop              Stop Tomcat"
      echo "                    are you running?"
      exit 1
    ;;
esac

