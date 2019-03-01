# 根据pfx 证书，获取公钥和私钥
1. 提取key：openssl pkcs12 -in xxx.pfx -nocerts -nodes -out 1.key
2. 导出公钥：openssl rsa -in 1.key -pubout -out 1_pub.key
3. 导出私钥：openssl rsa -in  1.key -out 1_pri.key
4. 将私钥转为PKCS 8格式：openssl pkcs8 -topk8 -nocrypt -in 1_pri.key -out 1_pri_pcks8

ps:
openssl pkcs12 -in xxx.pfx -nocerts -nodes -out 1.key
貌似这个获取的key，跟以上四步获取的一样




