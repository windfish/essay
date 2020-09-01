# jsvc 指令参数：http://commons.apache.org/proper/commons-daemon/jsvc.html

# jsvc 启动 java 服务

1. 下载 apache commons-daemon-1.1.0-src.tar.gz
2. 解压，commons-daemon-1.1.0.jar 启动时需要加入 CLASSPATH
3. 编译生成 jsvc：源码目录在 src/native/unix 中，./configure，然后make
4. 服务启动类需实现 org.apache.commons.daemon.Daemon 接口
5. 编写启动脚本 startup.sh


# jsvc 启动 tomcat（tomcat7）

1. tomcat-home/bin 下有 commons-daemon-native.tar.gz，解压
2. 进入 commons-daemon 目录的 unix 文件夹下，编译生成 jsvc（依次执行 ./configure, make），然后将 jsvc 拷贝到 bin 目录下
3. 可以在 bin 目录下创建 setenv.sh 设置 JVM 参数等信息
4. 执行 daemon.sh start 启动tomcat



# maven 项目打包方式

使用maven-jar-plugin 中的make-a-jar 来打包项目，打包出的jar 不包含依赖的jar 包，并且配置文件也独立于jar 包

这样打包的好处是：
1. jar 包不是fat jar，体积小，依赖的jar 包可以在第三方管理，不需要我们在jar 中去管理
2. 配置文件独立于jar，方便修改配置文件，而不需要重新打包

```
<plugin>
  <!-- 项目单独打jar包，不包含依赖的jar，依赖的jar放在WEB-INF/lib 下
       最终将war包解压，并将项目jar放入lib中，删除classes 文件夹，然后将解压的目录放入tomcat 启动
       配置文件也独立与jar包
   -->
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-jar-plugin</artifactId>
  <version>2.6</version>
  <executions>
      <execution>
          <id>make-a-jar</id>
          <phase>compile</phase>
          <goals>
              <goal>jar</goal>
              <goal>test-jar</goal>
          </goals>
          <configuration>
              <includes>
                  <include>com/**</include>
              </includes>
          </configuration>
      </execution>
  </executions>
</plugin>

<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-install-plugin</artifactId>
  <executions>
      <execution>
          <phase>install</phase>
          <goals>
              <goal>install-file</goal>
          </goals>
          <configuration>
              <packaging>jar</packaging>
              <groupId>${project.groupId}</groupId>
              <artifactId>${project.artifactId}</artifactId>
              <version>${project.version}</version>
              <file>
                  ${project.build.directory}/${project.artifactId}.jar
              </file>
              <!-- -${project.version} -->
          </configuration>
      </execution>
  </executions>
</plugin>

<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <configuration>
      <skip>true</skip>
  </configuration>
</plugin>
```
 
 
