JAVA_HOME=$JAVA_HOME
JAVA_OPTS="-server\
 -Xss256k\
 -Xms256M\
 -Xmx512M\
 -XX:MaxPermSize=192M\
 -XX:PermSize=128M\
 -XX:NewSize=128M\
 -XX:+CMSIncrementalMode\
 -XX:CMSInitiatingOccupancyFraction=80\
 -XX:+UseConcMarkSweepGC\
 -XX:+UseParNewGC\
 -XX:ParallelGCThreads=8\
 -Djavax.servlet.request.encoding=UTF-8\
 -Djavax.servlet.response.encoding=UTF-8\
 -Dfile.encoding=UTF-8\
 -Duser.timezone=Asia/Shanghai\
 -Djava.net.preferIPv4Stack=true\
 -Djava.net.preferIPv4Addresses=true"

echo "JAVA_HOME=$JAVA_HOME"
echo "JAVA_OPTS=$JAVA_OPTS"
