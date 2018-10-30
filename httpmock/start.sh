#!/bin/bash
# author xuliang
# since 2018-10-30
# moco github: https://github.com/dreamhead/moco
# 单配置文件模式
# example: 
#   curl -v http://localhost:12306/test
#   curl -v http://localhost:12306/ad

nohup java -jar moco-runner-0.12.0-standalone.jar http -p 12306 -c config.json &
