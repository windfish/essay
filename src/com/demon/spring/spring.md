# Spring IOC 容器
##### alias 
在Spring 中，可以使用 alias 标签给bean 起个别名，可通过别名获取bean；也可以给别名起别名，同样的也可以获取bean。@see com.demon.spring.learn.HelloAlias

##### FactoryBean
是一个接口，一种工厂Bean，用来生成一种Bean。实现类可以构造Bean，通过Spring 容器访问实现类时，会获得产生的Bean，若要访问FactoryBean，则需&FactoryBeanId。@see com.demon.spring.learn.HelloFactoryBean

##### Spring 工厂模式注入
@see com.demon.spring.learn.CarFactory
> 静态工厂模式：factory-method 标识静态工厂的工厂方法
> 非静态工厂模式：factory-bean 标识工厂bean，factory-method 标识工厂方法
