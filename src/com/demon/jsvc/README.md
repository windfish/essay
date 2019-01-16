# jsvc 指令参数：http://commons.apache.org/proper/commons-daemon/jsvc.html

# jsvc 启动 java 服务

1. 下载 apache commons-daemon-1.1.0-src.tar.gz
2. 解压，commons-daemon-1.1.0.jar 启动时需要加入 CLASSPATH
3. 编译生成 jsvc：源码目录在 src/native/unix 中，./configure，然后make
4. 服务启动类需实现 org.apache.commons.daemon.Daemon 接口
5. 编写启动脚本 startup.sh


# jsvc 启动 tomcat（tomcat7）

1. tomcat-home/bin 下有 commons-daemon-native.tar.gz，解压编译生成 jsvc，然后将 jsvc 拷贝到 bin 目录下
2. 可以在 bin 目录下创建 setenv.sh 设置 JVM 参数等信息
3. 执行 daemon.sh start 启动tomcat



 
