# moco http mock
前后端分离后，前端在接口文档确认后，即可通过moco 搭建的http mock 服务，来进行接口逻辑的调试，更好的关注自己的工作，而不必等待后端接口的完成，也不必作为接口的初步测试者。

#### github 地址：https://github.com/dreamhead/moco

### mock启动
```
java -jar moco-runner-0.12.0-standalone.jar http -p 12306 -c config.json
```
-p 指定端口  -c 指定配置文件

```
[
    { "context": "/pro1", "include": "pro1.json" }, 
    { "context": "/pro2", "include": "pro2.json" },
    { "context": "/", "include": "config.json" }
]
```
使用时，建议使用多文件配置，使用上下文来标识不同部门的接口mock，@see global.json
