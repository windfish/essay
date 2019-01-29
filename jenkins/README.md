# 背景
测试服务器上已有打包、重启、发布脚本，但每次发布还需要依次执行各个脚本，所以考虑使用Jenkins，每次测试服发布时，只需要点一次构建就可以了。
构建Jenkins 环境时，原有脚本还是有一定的修改。

# Jenkins 环境搭建
Jenkins 运行环境，需要java 支持，因此，需要先安装JDK，并配置相关的环境变量。
#### 搭建步骤
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
#### Jenkins 配置步骤
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
![](https://oscimg.oschina.net/oscnet/595b5ab943f320619d872eda9553890d3ad.jpg)

在这里填写具体的构建过程，我们选择“执行 Shell”。Jenkins 建议我们添加 -ex 参数来执行，那么所以的命令在执行之前就会被打印出来，方便在日志中查看Shell 的执行情况。
![](https://oscimg.oschina.net/oscnet/f9a294e30e4735f557caf9496cc1aabac29.jpg)

3. 执行构建

保存配置后，点击“立即构建”就可以执行当前的构建任务了，然后可以在“Build History”中查看每次构建的日志信息
![](https://oscimg.oschina.net/oscnet/d39bbd98d6cb9262613f7baee251d8c1886.jpg)

可以看到，每一条命令在执行之前，都会先将命令打印出来
![](https://oscimg.oschina.net/oscnet/65e1958ef9a011b8e3eda052061db42b969.jpg)

**注意**
Jenkins 会默认将子进程kill 掉，需要在系统管理，配置环境变量

> Name：BUILD_ID

> Value：allow_to_run_as_daemon start_my_service

# Jenkins 远程管理服务
基本流程是利用Jenkins SSH 到远程机器上，然后执行Shell 脚本
#### 必要条件
1. Publish Over SSH插件

在Jenkins 的 系统管理 --> 插件管理，搜索Publish Over SSH 插件并安装。然后在系统管理中，就可以配置SSH 
![](https://oscimg.oschina.net/oscnet/57e975d4028008477fc2bdff2b893879c9e.jpg)

SSH Servers 中配置SSH Server，包含hostname、username、remote directory，在Advanced 中可以录入远程机器的用户密码

2. 专用的用户

#### 配置步骤

构建步骤中选择远程shell 
![](https://oscimg.oschina.net/oscnet/fa1581d3992357ec1dc510af32c151dadfb.jpg)

有几点需要注意：
1. linux sudo 需要处理，配置sudo 不需要输入密码

设置方法为visudo，增加以下配置：
```
xuliang ALL=NOPASSWD:ALL
```

2. Shell 脚本中需要#!/bin/bash -il，否则会出现无法读取环境变量的问题。
-i 交互式Shell；-l 登录式Shell
![](https://oscimg.oschina.net/oscnet/f67984763ed5daec02c59dc5966533ef28f.jpg)


