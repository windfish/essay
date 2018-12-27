# Spring IOC 容器
##### id 和name
Spring 容器中每个Bean 都有唯一的名字（beanName），和0个或多个别名（alias）：
1. 设置了id，那么beanName 为id 的值，无别名
2. 设置了id 和name，那么beanName 为id 的值，别名为name 的值，别名可以设置多个，使用逗号分隔
3. 设置了name，那么beanName 为name 的第一个值，别名为name 其他的值
4. 没设置id 和name，那么Spring 会以class 生成beanName 和别名

##### 相同的id 或name 的bean，Spring 的处理方式
1. 同一个配置文件中，会抛出异常
2. 不同配置文件中，不会抛出异常，会由后面的bean 覆盖前面的bean
不同配置文件中，覆盖后可能会出现问题，解决方案是：设置 allowBeanDefinitionOverriding 的属性为false
1. web 环境下，编写ApplicationContextInitializer 接口的实现类，将allowBeanDefinitionOverriding 的值设置为false；并在web.xml 中配置相应的参数
@see com.demon.spring.learn.XmlApplicationContextInitializer
```
// 针对web 环境，在web.xml 中添加配置：
<context-param>
    <param-name>contextInitializer</param-name>
    <param-value>com.demon.spring.learn.XmlApplicationContextInitializer</param-value>
</context-param>
// 对于spring mvc，添加如下配置：
<init-param>
    <param-name>contextInitializer</param-name>
    <param-value>com.demon.spring.learn.XmlApplicationContextInitializer</param-value>
</init-param>
```
2. java 服务环境下，设置allowBeanDefinitionOverriding 为false 后，刷新applicationContext
```
applicationContext.setAllowBeanDefinitionOverriding(false);
applicationContext.refresh();
```

##### alias 
在Spring 中，可以使用 alias 标签给bean 起个别名，可通过别名获取bean；也可以给别名起别名，同样的也可以获取bean。@see com.demon.spring.learn.HelloAlias

##### FactoryBean
是一个接口，一种工厂Bean，用来生成一种Bean。实现类可以构造Bean，通过Spring 容器访问实现类时，会获得产生的Bean，若要访问FactoryBean，则需&FactoryBeanId。@see com.demon.spring.learn.HelloFactoryBean
注意：
1. singleton 类型的FactoryBean 生成的Bean 实例也认为是单例的
2. prototype 类型的FactoryBean 生成的Bean 实例，每次都会创建新的实例

##### Spring 工厂模式注入
@see com.demon.spring.learn.CarFactory
> 静态工厂模式：factory-method 标识静态工厂的工厂方法
> 非静态工厂模式：factory-bean 标识工厂bean，factory-method 标识工厂方法

##### lookup-method
该特性会在运行时，对prototype 类型的引用，每次都返回一个新的实例。
通过BeanFactory 的getBean 获取实例时，对于singleton 类型的bean，每次都返回同一个实例；对于prototype 类型的bean，会返回一个新的实例。
例如：一个singleton 类型的bean 有一个prototype 类型的成员变量，那么在初始化singleton 实例时，会向其注入一个prototype 类型的实例，但是singleton 实例只会实例化一次，那么内部的prototype 的成员变量就不会改变，那么如何使内部的变量改变呢？
@see com.demon.spring.learn.lookup_method.NewsProvider
1. singleton 类实现ApplicationContextAware 接口或BeanFactoryAware 接口，在get prototype 变量时，从Spring 容器中再获取一个新的实例返回
2. 使用lookup-method，Spring 会对singleton 实例进行增强，使其每次get prototype 变量时，都返回一个新的实例

##### depends-on
当一个bean 直接依赖另一个bean 时，可以使用<ref/>标签。当某一个bean 不直接依赖另一个bean，但是需要bean 先实例化好，这时候需要使用depends-on 特性

##### BeanPostProcessor
bean 实例化的后置处理器，是Spring 的一个扩展点，通过实现该接口，可以查收bean 实例化的过程。例如AOP 就是在bean 实例化时，将切面逻辑织入bean 实例的。@see com.demon.spring.learn.LoggerBeanPostProcessor

##### Aware 接口
Spring 中定义了一些Aware 接口，通过这些接口，我们可以在运行时获取一些配置信息或一些其他信息，比如：BeanNameAware 接口，可以获取bean 的配置名称；BeanFactoryAware 接口，可以在运行时获取BeanFactory 实例；ApplicationContextAware 接口，可以再运行时获取ApplicationContext 实例。

##### 初始化bean 的回调
1. xml 中配置init-method，指定回调方法
2. bean 实现InitializingBean 接口
3. 使用@Bean(initMethod="init")注解，指定回调方法
4. 使用@PostConstruct 注解
@see com.demon.spring.learn.BeanCallback

##### 销毁bean 的回调
1. xml 中配置destroy-method，指定回调方法
2. bean 实现DisposableBean 接口
3. 使用@Bean(destroyMethod = "cleanup") 注解，指定回调方法
4. 使用@PreDestroy 注解
@see com.demon.spring.learn.BeanCallback

