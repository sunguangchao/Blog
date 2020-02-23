### 1.Spring IoC的流程

1. 第一个阶段是容器启动阶段

   1. 加载配置文件-BeanDefinitionReader
   2. 解析配置文件：经过PropertyEditor定义的规则将字符串转化成对应的类型信息之后存储在BeanDefinition中共，交给BeanDefinitionRegisty管理，容器内的启动过程完成

2. 第二个阶段是Bean初始化阶段（非懒加载单例bean）

   1. 实例化Bean对象
   2. 设置对象属性-populateBean()
   3. 检查Aware相关接口并设置相关依赖-BeanNameAware和BeanFactoryAware接口等
   4. BeanPostProcessor的前置处理
   5. 调用初始化方法
      1. InitializingBean的afterPropertiesSet()
      2. init-method
   6. BeanPostProcessor的后置处理
   7. 注册需要执行销毁方法的bean

   reference:[https://www.cnblogs.com/firepation/p/9584764.html](https://www.cnblogs.com/firepation/p/9584764.html)

### 2.BeanFactory和FactoryBean的区别

1. BeanFactory是接口，提供了IOC容器最基本的形式，给具体的IOC容器的实现提供了规范
2. FactoryBean也是接口，为IOC容器中bean的实现提供了更加灵活的方式，FactoryBean在IOC容器的基础上给Bean的实现加上了一个简单工厂模式和装饰器模式。我们可以在getObject()方法中灵活配置。其实在Spring源码中有很多FactoryBean的实现类.
3. 总结：BeanFactory是一个Factory，也就是IoC容器或对象工厂，FactoryBean是个特殊的bean。在Spring中，**所有的Bean都是由BeanFactory(也就是IOC容器)来进行管理的**。但对FactoryBean而言，**这个Bean不是简单的Bean，而是一个能生产或者修饰对象生成的工厂Bean,它的实现与设计模式中的工厂模式和修饰器模式类似 。**
4. reference:[https://www.cnblogs.com/aspirant/p/9082858.html](https://www.cnblogs.com/aspirant/p/9082858.html)





### 3.Spring 的 AOP 是怎么实现的



### 4.Spring 的事务传播行为有哪些，讲下嵌套事务



### 5.什么情况下对象不能被代理



### 6.Spring 怎么解决循环依赖的问题

先说结论：

1. Spring无法解决singleton范围的构造器注入构成的循环依赖，可以解决setter循环依赖。
2. 不能解决prototype范围的循环依赖。

```java
一级缓存：
/** 保存所有的singletonBean的实例 */
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>(64);

二级缓存：
/** 保存所有早期创建的Bean对象，这个Bean还没有完成依赖注入 */
private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);
三级缓存：
/** singletonBean的生产工厂*/
private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>(16);
 
/** 保存所有已经完成初始化的Bean的名字（name） */
private final Set<String> registeredSingletons = new LinkedHashSet<String>(64);
 
/** 标识指定name的Bean对象是否处于创建状态  这个状态非常重要 */
private final Set<String> singletonsCurrentlyInCreation =
    Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>(16));

```

假设A和B通过setter方式循环依赖

1. 在实例化之前，会调用getSingleton()方法，看缓存中有没有A，没有的话，先通过构造方法实例化一个A
2. 实例化A之后，调用addSingletonFactory()方法将A加入到三级缓存中
3. A的属性注入，调用populateBean()方法，发现需要依赖B
4. B的步骤和前面三步一样，走到了populateBean()方法，这时候需要获取A，调用doGetBean(A)，而A已经加入到缓存里了，所以是可以获取到A。
5. 通过将实例提前暴露到缓存singletonFactories里，解决了循环依赖的问题。

为什么不能解决singleton范围的构造器注入构成的循环依赖，因为因为A中构造器注入了B，那么A在关键的方法addSingletonFactory()之前就去初始化了B，导致三级缓存中根本没有A，所以会发生死循环，Spring发现之后就抛出异常了(BeanCurrentlyInCreationException)

不能解决prototype范围的循环依赖，是因为没有三级缓存这个机制

参考：[https://www.jianshu.com/p/8bb67ca11831](https://www.jianshu.com/p/8bb67ca11831)



### 7.要在Spring IoC容器构建完毕后执行一些逻辑，怎么实现？

1. **ApplicationContextAware.setApplicationContext**
2. **Bean 添加了@PostConstruct的方法**
3. **InitializingBean.afterPropertiesSet**
4. **BeanPostProcessor （postProcessBeforeInitialization、postProcessAfterInitialization）**
5. **SmartLifecycle.start**
6. **ApplicationListener.onApplicationEvent**
7. **ApplicationRunner.run**

### 8.@Resource 和 @Autowire 的区别

[https://blog.csdn.net/xxliuboy/article/details/86441832](https://blog.csdn.net/xxliuboy/article/details/86441832)



### 9.@Autowire 怎么使用名称来注入

```java
@Autowire
@Qualifier("beanName")
private InjectionBean beanName;
```

### 10.bean 的 init-method 属性指定的方法里用到了其他 bean 实例，会有问题吗



### 11.@PostConstruct 修饰的方法里用到了其他 bean 实例，会有问题吗

Constructor(构造方法) -> @Autowired(依赖注入) -> @PostConstruct(注释的方法)



### 12.Spring 中，有两个id 相同的bean，会报错吗，如果会报错，在哪个阶段报错

不会，allowBeanDefinitionOverriding=true默认，所以默认是覆盖的，

这个过程发生在配置解析的过程中

[https://www.jianshu.com/p/820bcd48d4bc](https://www.jianshu.com/p/820bcd48d4bc)

### 13.Spring 中，bean的class 属性指定了一个不存在的class，会报错吗，如果会报错，在哪个阶段



### 14.Spring 中的常见扩展点有哪些

