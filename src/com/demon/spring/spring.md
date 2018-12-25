# Spring IOC 容器
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
bean 实例化的后置处理器，是Spring 的一个扩展点，通过实现该接口，可以查收bean 实例化的过程。例如AOP 就是在bean 实例化时，将切面逻辑织入bean 实例的。

##### Aware 接口
Spring 中定义了一些Aware 接口，通过这些接口，我们可以在运行时获取一些配置信息或一些其他信息，比如：BeanNameAware 接口，可以获取bean 的配置名称；BeanFactoryAware 接口，可以在运行时获取BeanFactory 实例；ApplicationContextAware 接口，可以再运行时获取ApplicationContext 实例。


