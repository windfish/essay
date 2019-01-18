# 插件地址

##### lua-resty-http： https://github.com/ledgetech/lua-resty-http

##### lua-cjson： https://github.com/mpx/lua-cjson

##### lua-resty-redis： https://github.com/openresty/lua-resty-redis

##### lua-resty-kafka： https://github.com/doujiang24/lua-resty-kafka


# 缓存
### 配置缓存
```
proxy_cache_path /data/platform/logs/nginx.cache levels=1:2 keys_zone=nginx_cache:200m max_size=10g 
                 loader_threshold=300 loader_files=200 inactive=60m use_temp_path=off;
```
在nginx 的http 上下文的顶层位置配置 proxy_cache_path

* /data/platform/logs/nginx.cache 本地路径，缓存的存放地址
* levels 设置缓存文件的目录层数，默认为一层。levels=1:2，表示两层目录，例如缓存的key值为1d4f46c8b32f806412bcf540328c76d0，那么最后一位 0 作为第一级目录，倒数第2~3位 6d 作为第二级目录，也就是其存放地址为 /data/platform/logs/nginx.cache/0/6d/1d4f46c8b32f806412bcf540328c76d0
* keys_zone 在共享内存中设置一块存储区域来存放缓存的key 和metadata，这样nginx 可以快速判断一个request 是否命中缓存。包含共享内存的缓存名称和占用的最大共享内存大小
* max_size 缓存占用的最大的硬盘空间，当达到最大值时，会删除使用最少的缓存内容
* inactive 未被访问的文件在缓存中的保存时间，即60分钟不被访问，则缓存文件会被删除。inactive 和expired 含义是不同的，expired 只是过期，不会被删除，而inactive 在指定时间内未被访问，则会删除对应文件
* use_temp_path 如果为off，则nginx 会将缓存文件直接写入指定的cache 文件中，而不是使用temp_path 存储。官方建议off，避免文件在不同文件系统中不必要的拷贝
* loader_threshold loader_files 缓存进程在工作时，会通过加载器将缓存的元数据加载到共享内存中，如果一次性加载全部缓存信息，会大量消耗资源，使nginx 在启动后的几分钟内变得很慢，则可通过这两个参数来指定加载策略。loader_threshold 指定每次加载执行的时间，loader_files 指定每次加载的文件数量

### 使用缓存
```
location /api/ad/status {
    proxy_cache nginx_cache;
    proxy_cache_valid 10m;
}
```
然后在目标上下文中使用 proxy_cache 指令

* proxy_cache 启用缓存，并指定key_zone。如果proxy_cache off 表示关闭缓存
* proxy_cache_valid 设置缓存时长，可以为不同的http 状态码设置不同的缓存时长，命令格式为 proxy_cache_valid [code] time;

### 缓存命中情况
可以通过nginx 的变量$upstream_cache_status 来获取缓存的命中情况，其返回值可能为：

* MISS 响应在缓存中找不到，所以需要在服务器中取得。这个响应之后可能会被缓存起来
* BYPASS 响应来自原始服务器而不是缓存，因为请求匹配了一个proxy_cache_bypass，这个响应之后可能会被缓存起来
* EXPIRED 缓存中的某一项过期了，来自原始服务器的响应包含最新的内容
* STALE 内容陈旧是因为原始服务器不能正确响应。需要配置proxy_cache_use_stale
```
location / {
    proxy_cache_use_stale error timeout http_500 http_502 http_503 http_504;
}
```
表示开启容错能力，若源站出现问题（timeout、500、502 等），且缓存中存在请求的陈旧版本，则会将陈旧版本而不是错误信息返回给客户端
* UPDATING 内容过期了，因为相对于之前的请求，响应的入口（entry）已经更新，并且proxy_cache_use_stale的updating已被设置
* REVALIDATED proxy_cache_revalidate命令被启用，NGINX检测得知当前的缓存内容依然有效（If-Modified-Since或者If-None-Match）
* HIT 响应包含来自缓存的最新有效的内容

### 缓存判断机制

![](https://oscimg.oschina.net/oscnet/6d71cdf35c576ce1c94b3eb428ad501323b.jpg)


# OpenResty 里使用Lua

在请求html 文件时，记录页面被访问的pv
```
location ~ .*\.(html|htm|txt|js|css)$ {
    access_by_lua_file '/data/platform/nginx-scripts/igame.com.t/ad.nginx.pv.lua';
    expires 10m;
}
```

### nginx 执行步骤
nginx 在处理每一个用户请求时，都是按照若干个不同的阶段依次处理的，与配置文件上的顺序没有关系
1. post-read：读取请求内容的阶段，nginx 读取并解析完请求头之后立即开始执行。例如nginx_realip 就在该阶段注册了处理程序，用来将当前请求的来源地址指定为某一请求头的值
2. server-rewrite：server 块中请求地址重写阶段；当nginx_rewrite 模块的rewrite、set 配置指令直接书写在server 配置块中时，基本上都是运行在server-rewrite 阶段
3. find-config：配置查找阶段，用来完成当前请求与location 配重块之间的配对工作。这个阶段并不支持nginx 注册处理程序，是由nginx 核心来完成的
4. rewrite：location 块中请求地址重写阶段。nginx\_rewrite 模块的rewrite 指令用于location 中时，就在这个阶段运行；ngx\_set_misc（设置MD5、encode_base64等）模块的指令，ngx\_lua 模块的set\_by\_lua 和rewrite\_by_lua 指令也在这个阶段执行
5. post-rewrite：请求地址重写提交阶段，由nginx 核心完成rewrite 阶段所要求的“内部跳转”操作
6. pre-access：访问权限检测准备阶段，标准模块ngx\_limit\_req 和ngx\_limit_zone 就在此阶段运行，前者可以控制请求的访问频度，而后者可以限制访问的并发度
7. access：访问权限检查阶段，标准模块ngx\_access、第三方模块ngx\_auth\_request 以及ngx\_lua 的access\_by_lua 指令就运行在这个阶段。配置的指令多是执行访问控制性质的任务，比如检查用户的访问权限，检查用户的来源 IP 地址是否合法
8. post-access：访问权限检查提交阶段；主要用于配合 access 阶段实现标准 ngx\_http_core 模块提供的配置指令 satisfy 的功能。
9. try-files：配置项 try\_files 处理阶段；专门用于实现标准配置指令 try_files 的功能 如果前 N-1 个参数所对应的文件系统对象都不存在，try-files 阶段就会立即发起“内部跳转”到最后一个参数（即第 N 个参数）所指定的 URI
10. content：内容产生阶段，是所有请求处理阶段中最为重要的阶段，因为这个阶段的指令通常是用来生成HTTP响应内容的；nginx 的 content 阶段是所有请求处理阶段中最为重要的一个，因为运行在这个阶段的配置指令一般都肩负着生成“内容” 并输出 HTTP 响应的使命。
11. log：日志模块处理阶段；记录日志

**nginx\_lua 的具体执行阶段参考 http://www.cnblogs.com/JohnABC/p/6206622.html**

_satisfy 功能_

如果在一个字段中同时使用了ngx_http_access_module 模块和Auth Basic 模块的指令，可以使用这个指令确定一种验证方式：
* all - 必须同时匹配Access和Auth Basic中指令指定的权限。 
* any - 具有Access 或 Auth Basic指令任一权限即可通过匹配 
allow、deny 属于ngx_http_access_module 模块
auth_basic、auth_basic_user_file 属于Auth Basic 模块
```
location / {
      satisfy any;
      allow 192.168.1.0/32;
      deny all;
      auth_basic "closed site";
      auth_basic_user_file conf/htpasswd;
}
```


# proxy_pass
```
server {
    listen      80;
    server_name test.demon.com;
    
   # 访问 http://test.demon.com/test1/api/test
   # 后端的request_uri 为 /test1/api/test
   location /test1 {
        proxy_pass  http://test1.demon.com
   }
   
   # 访问 http://test.demon.com/test1/api/test
   # 后端的request_uri 为 /api/test
   location /test2 {
        proxy_pass  http://test2.demon.com/
   }
   
   # 第二个proxy_pass 的后面带了“/”，会在代理后，忽略匹配的uri（/test2），第一种不会忽略
    
}
```

