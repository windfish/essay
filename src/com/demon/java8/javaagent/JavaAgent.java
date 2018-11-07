package com.demon.java8.javaagent;

/**
 * javaagent+javassist 修改字节码，进行代码注入
 * 
 * @see 具体代码参见 agent.7z
 * <pre>
 * 例子中，为每个方法注入了执行开始时间和执行结束时间的输出
 * 1、先创建MainAgent 并实现ClassFileTransformer 接口，在transform 方法里做字节码的修改
 * 2、再创建Agent，并编写premain 方法，其在main 方法执行前执行，用于将MainAgent 注册到系统中
 * 3、在MANIFEST.MF 文件中有Premain-Class。在maven 打包时增加配置
 *      <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.0.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <manifestEntries>
                                        <Premain-Class>com.demon.javaagent.Agent</Premain-Class>
                                    </manifestEntries>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
 * 4、打包，在启动JVM 时，增加参数 -javaagent:agent-0.0.1-SNAPSHOT.jar
 * </pre>
 * 
 * @author xuliang
 * @since 2018年11月7日 下午10:04:40
 *
 */
public class JavaAgent {

    public void hello(){
        System.out.println("hello javaagent");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void test(){
        System.out.println("no javaagent out");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        JavaAgent a = new JavaAgent();
        a.test();
        System.out.println("--------------------");
        a.hello();
        System.out.println("--------------------");
        new Test().print();
    }
    
}
