# 目录
* [Jenkins 环境搭建](https://github.com/windfish/java-essay/tree/master/jenkins#jenkins-%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA)
* [Jenkins 管理本地服务](https://github.com/windfish/java-essay/tree/master/jenkins#Jenkins-%E7%AE%A1%E7%90%86%E6%9C%AC%E5%9C%B0%E6%9C%8D%E5%8A%A1)
* [Jenkins 管理测试服](https://github.com/windfish/java-essay/tree/master/jenkins#Jenkins-%E7%AE%A1%E7%90%86%E6%B5%8B%E8%AF%95%E6%9C%8D)
* [Jenkins 管理正式服](https://github.com/windfish/java-essay/tree/master/jenkins#Jenkins-%E7%AE%A1%E7%90%86%E6%AD%A3%E5%BC%8F%E6%9C%8D)
* [遇到的问题汇总](https://github.com/windfish/java-essay/tree/master/jenkins#%E9%81%87%E5%88%B0%E7%9A%84%E9%97%AE%E9%A2%98%E6%B1%87%E6%80%BB)

# 背景
测试服务器上已有打包、重启、发布脚本，但每次发布还需要依次执行各个脚本，所以考虑使用Jenkins，每次测试服发布时，只需要点一次构建就可以了。
构建Jenkins 环境时，原有脚本还是有一定的修改。

# Jenkins 环境搭建
Jenkins 运行环境，需要java 支持，因此，需要先安装JDK，并配置相关的环境变量。
### 搭建步骤
1. 下载Jenkins

我下载是war包，启动后支持浏览器访问Jenkins，下载地址为[Jenkins Download](http://mirrors.jenkins.io/war-stable/latest/jenkins.war)，也可以到官网上自行操作。

2. 启动Jenkins

官网上的教程，是直接启动（java -jar jenkins.war --httpPort=8080），那么当你断开终端连接或者Ctrl+C 中断操作时，会将Jenkins 一并关闭。因此，需要编写执行脚本来启动
```
cd /data/jenkins && pwd
nohup java -jar jenkins.war --httpPort=8888 2>&1 &
```

3. 访问Jenkins 控制台

在浏览器里输入http://IP:8888 即可访问Jenkins 控制台，第一次访问控制台，会提示设置管理员和密码，并且会提示安装Jenkins 插件，按需操作即可


# Jenkins 管理本地服务
通过本地Shell 脚本管理本地服务，Shell 脚本最好在机器上编写，仅适用Jenkins 来调用，方便管理Shell 脚本
### Jenkins 配置步骤
以moco 服务为例

1. 编写脚本
moco 服务管理脚本 start-jenkins.sh
```
#!/bin/bash
# author xuliang
# since 2019-1-11

# moco start、stop、restart

cd /data/moco && pwd

PID=`. ps.sh moco-runner | grep -v grep | awk '{print $2}'|awk '{print $1}'`
if [ ".$PID" == "." ]; then
        bash -x start-global.sh
        exit
fi

echo "stop moco server."
kill -9 $PID

PID=`. ps.sh moco-runner | grep -v grep | awk '{print $2}'|awk '{print $1}'`
if [ ".$PID" == "." ]; then
        echo "moco stop success."
fi

bash -x start-global.sh
```

2. Jenkins 配置

在Jenkins 主页左边的菜单里，点击“New 任务”，然后输入任务名称并选择“自由风格的软件项目”，下一步可以看到任务的详细内容配置。

在这里填写项目的基本信息
![](https://github.com/windfish/img/blob/master/notes-img/jenkins/595b5ab943f320619d872eda9553890d3ad.jpg)

在这里填写具体的构建过程，我们选择“执行 Shell”。Jenkins 建议我们添加 -ex 参数来执行，那么所以的命令在执行之前就会被打印出来，方便在日志中查看Shell 的执行情况。
![](https://github.com/windfish/img/blob/master/notes-img/jenkins/f9a294e30e4735f557caf9496cc1aabac29.jpg)

3. 执行构建

保存配置后，点击“立即构建”就可以执行当前的构建任务了，然后可以在“Build History”中查看每次构建的日志信息
![](https://oscimg.oschina.net/oscnet/d39bbd98d6cb9262613f7baee251d8c1886.jpg)

可以看到，每一条命令在执行之前，都会先将命令打印出来
![](https://oscimg.oschina.net/oscnet/65e1958ef9a011b8e3eda052061db42b969.jpg)


# Jenkins 管理测试服
基本流程是利用Jenkins SSH 到内网测试服机器上，然后执行Shell 脚本
### 必要条件
1. Publish Over SSH插件

在Jenkins 的 系统管理 --> 插件管理，搜索Publish Over SSH 插件并安装。然后在系统管理中，就可以配置SSH 
![](https://oscimg.oschina.net/oscnet/3d2a778c703a9fd3d86e676cbbba31990f5.jpg)

SSH Servers 中配置SSH Server，包含hostname、username、remote directory，在Advanced 中可以录入远程机器的用户密码

2. 专用的用户，例如：jenkins

### 配置步骤

构建步骤中选择远程shell 
![](https://oscimg.oschina.net/oscnet/97f6bf1c6d3dcb8442918345a1954f15c63.jpg)


# Jenkins 管理正式服
### 基本流程
1. SSH 到内网测试服，package 项目
2. rsync 打包的文件到正式服
3. SSH 到正式服，执行相应的脚本

### 构建rsync 服务
1. 安装rsync

使用 yum install rsync -y 安装

2. 配置/etc/rsyncd.conf

```
log file=/var/log/rsync.log         # 日志文件
pid file=/var/run/rsyncd.pid        # pid 文件
use chroot=false                    # 表示在传输文件前首先chroot到path参数所指定的目录下。这样做的原因是实现额外的安全防护，但缺点是需要以roots权限，并且不能备份指向外部的符号连接所指向的目录文件。默认情况下chroot值为true，如果你的数据当中有软连接文件，建议设置成false
[deploy]                            # 自定义模块名
read only=false                     # 如果为true，则不能上传到该模块指定的路径下
uid=root                            # 指定传输文件时以哪个用户的身份传输
gid=root                            # 指定传输文件时以哪个组的身份传输
path=/home/jenkins/publish          # 指定数据存放的路径
auth users=jenkins                  # 指定传输时要使用的用户名
secrets file=/etc/rsyncd.passwd     # 指定密码文件，该参数连同上面的参数如果不指定，则不使用密码验证。注意该密码文件的权限一定要是600。格式：用户名:密码
```

3. 启动rsync 服务

```
sudo rsync --daemon
```

### 配置构建逻辑

1. SSH 到测试服，打包项目，并rsync 到正式服务器
![](https://oscimg.oschina.net/oscnet/83781b90dfb91c0c139a26b4324289bf8f8.jpg)

2. SSH 到正式服，重启服务
![](https://oscimg.oschina.net/oscnet/68704998e37916242a42fa742ef834f0428.jpg)



# 遇到的问题汇总
#### jenkins 执行shell 后，会kill 掉子进程
需要在系统管理，配置环境变量

> Name：BUILD_ID

> Value：allow_to_run_as_daemon start_my_service

#### linux sudo 需要处理，配置sudo 不需要输入密码

设置方法为visudo，增加以下配置：
```
jenkins ALL=NOPASSWD:ALL
```

#### Shell 脚本中需要#!/bin/bash -il，否则会出现无法读取环境变量的问题。
-i 交互式Shell；-l 登录式Shell
![](https://oscimg.oschina.net/oscnet/f67984763ed5daec02c59dc5966533ef28f.jpg)

#### 远程执行shell 时，提示 sudo: sorry, you must have a tty to run sudo

提示这个是因为sudo 执行时，默认需要打开控制终端。
解决方案：修改sudo 配置文件，设置某个用户或用户组执行sudo 不需要打开控制终端
```
Defaults    requiretty               # 默认配置，都需要用户终端
Defaults:jenkins    !requiretty      # jenkins 用户不需要控制终端
Defaults:%operators    !requiretty   # operators 组不需要控制终端
```

#### rsync 需要录入密码

解决方式有两种（我选择第二种方式）：
* 一种是通过远程rsync 的方式，配置SSH 免密认证，rsync -avrI -e 'ssh -p ${PORT}' ${FILE} ${USER}@${HOST}:${PATH}
* 另一种是通过rsync 服务的方式，配置rsync 服务专用密码，客户端可以通过指定密码文件来免密同步，rsync -avrI ${FILE} ${USER}@${HOST}::${MODULE} --password-file=${PASSWD}

#### 重启操作（restart）非正常退出时，需要再次进行启动操作（start）
需要判断脚本的shell 退出标识，不为0时，需要进行start 操作。
sleep 是为了防止前一个shell 还未退出，后一个shell 执行失败的情况
```
./jmain-igame-asyn_log_to_db_jenkins.sh restart
result=$?
echo "------------result:$result"
if [ "$result" != "0" ]; then
  sleep 3
  echo "start asyn_log"
  ./jmain-igame-asyn_log_to_db_jenkins.sh start
fi
```

