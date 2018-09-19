#!/bin/bash

CLASSPATH=
for jar in /root/server/chatroom_server/lib/*.jar
do
        CLASSPATH=$CLASSPATH:$jar
done

CLASSPATH="$CLASSPATH:/root/server/chatroom_server/classes:/root/server/jsvc_home/commons-daemon-1.1.0.jar"

echo "CLASSPATH=$CLASSPATH"

JMAIN_OUT="/root/server/chatroom_server/jmain-daemon.out"
JMAIN_PID=/root/server/chatroom_server/jmain-daemon.pid

MAIN=com.demon.netty.chapter11.chatroom.ChatRoomServer


case "$1" in
    start   )
      /root/server/jsvc_home/jsvc -procname "chatroom" \
      -pidfile "$JMAIN_PID" \
      -java-home "$JAVA_HOME" \
      -errfile "$JMAIN_OUT" \
      -classpath "$CLASSPATH" \
      $MAIN
      exit $?
    ;;
    stop    )
      /root/server/jsvc_home/jsvc -stop \
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

