#!/bin/bash
# author xuliang
# since 2018-10-30
# moco github: https://github.com/dreamhead/moco
# 多配置文件模式
# example:
#   curl -v http://localhost:12306/pro1/test
#   curl -v http://localhost:12306/pro2/test

nohup java -jar moco-runner-0.12.0-standalone.jar http -p 12306 -g global.json &
