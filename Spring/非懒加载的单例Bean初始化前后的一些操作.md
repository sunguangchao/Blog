```java
public void refresh() throws BeansException, IllegalStateException {
    synchronized (this.startupShutdownMonitor) {
        // Prepare this context for refreshing.
        prepareRefresh();

        // Tell the subclass to refresh the internal bean factory.
        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

        // Prepare the bean factory for use in this context.
        prepareBeanFactory(beanFactory);

        try {
            // Allows post-processing of the bean factory in context subclasses.
            postProcessBeanFactory(beanFactory);

            // Invoke factory processors registered as beans in the context.
            invokeBeanFactoryPostProcessors(beanFactory);

            // Register bean processors that intercept bean creation.
            registerBeanPostProcessors(beanFactory);

            // Initialize message source for this context.
            initMessageSource();

            // Initialize event multicaster for this context.
            initApplicationEventMulticaster();

            // Initialize other special beans in specific context subclasses.
            onRefresh();

            // Check for listener beans and register them.
            registerListeners();

            // Instantiate all remaining (non-lazy-init) singletons.
            finishBeanFactoryInitialization(beanFactory);

            // Last step: publish corresponding event.
            finishRefresh();
        }

        catch (BeansException ex) {
            // Destroy already created singletons to avoid dangling resources.
            destroyBeans();

            // Reset 'active' flag.
            cancelRefresh(ex);

            // Propagate exception to caller.
            throw ex;
        }
    }
}
```

前面重点分析的是`finishBeanFactoryInitialization()`方法，这个方法完成了所有非懒加载的单例bean的初始化，refresh()方法中还有一些其他比较重要的方法。

### prepareBeanFactory

```java
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // Tell the internal bean factory to use the context's class loader etc.
    beanFactory.setBeanClassLoader(getClassLoader());
    beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver());
    beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this));

    // Configure the bean factory with context callbacks.
    beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
  	//如果bean是这些接口的实现类，则不会被自动装配
    beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
    beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

    // BeanFactory interface not registered as resolvable type in a plain factory.
    // MessageSource registered (and found for autowiring) as a bean.
    beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
    beanFactory.registerResolvableDependency(ResourceLoader.class, this);
    beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
    beanFactory.registerResolvableDependency(ApplicationContext.class, this);

    // Detect a LoadTimeWeaver and prepare for weaving, if found.
  	//如果自定义的Bean中有定义过一个名为"loadTimeWeaver"的Bean，则会添加一个LoadTimeWeaverAwareProcessor
    if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
        beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
        // Set a temporary ClassLoader for type matching.
        beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
    }

    // Register default environment beans.
  	//如果自定义的Bean中没有名为"systemProperties"和"systemEnvironment"的Bean，则注册两个Bena，Key为"systemProperties"和"systemEnvironment"，Value为Map，这两个Bean就是一些系统配置和系统环境信息
    if (!beanFactory.containsBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
        Map systemProperties;
        try {
            systemProperties = System.getProperties();
        }
        catch (AccessControlException ex) {
            systemProperties = new ReadOnlySystemAttributesMap() {
                @Override
                protected String getSystemAttribute(String propertyName) {
                    try {
                        return System.getProperty(propertyName);
                    }
                    catch (AccessControlException ex) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Not allowed to obtain system property [" + propertyName + "]: " +
                                    ex.getMessage());
                        }
                        return null;
                    }
                }
            };
        }
        beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, systemProperties);
    }

    if (!beanFactory.containsBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
        Map<String,String> systemEnvironment;
        try {
            systemEnvironment = System.getenv();
        }
        catch (AccessControlException ex) {
            systemEnvironment = new ReadOnlySystemAttributesMap() {
                @Override
                protected String getSystemAttribute(String variableName) {
                    try {
                        return System.getenv(variableName);
                    }
                    catch (AccessControlException ex) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Not allowed to obtain system environment variable [" + variableName + "]: " +
                                    ex.getMessage());
                        }
                        return null;
                    }
                }
            };
        }
        beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, systemEnvironment);
    }
}
```

### invokeBeanFactoryPostProcessors

这个是整个Spring流程中非常重要的一部分，是Spring留给用户的一个非常有用的扩展点，**BeanPostProcessor接口针对的是每个Bean初始化前后做的操作；而BeanFactoryPostProcessor接口针对的是所有Bean实例化前的操作**，注意用词，初始化只是实例化的一部分，表示的是调用Bean的初始化方法，**BeanFactoryPostProcessor接口方法调用时机是任意一个自定义的Bean被反射生成出来前**。



```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    // Invoke BeanDefinitionRegistryPostProcessors first, if any.
    Set<String> processedBeans = new HashSet<String>();
    if (beanFactory instanceof BeanDefinitionRegistry) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        List<BeanFactoryPostProcessor> regularPostProcessors = new LinkedList<BeanFactoryPostProcessor>();
        List<BeanDefinitionRegistryPostProcessor> registryPostProcessors =
                new LinkedList<BeanDefinitionRegistryPostProcessor>();
        for (BeanFactoryPostProcessor postProcessor : getBeanFactoryPostProcessors()) {
            if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                BeanDefinitionRegistryPostProcessor registryPostProcessor =
                            (BeanDefinitionRegistryPostProcessor) postProcessor;
                    registryPostProcessor.postProcessBeanDefinitionRegistry(registry);
                    registryPostProcessors.add(registryPostProcessor);
            }
            else {
                regularPostProcessors.add(postProcessor);
            }
        }
        Map<String, BeanDefinitionRegistryPostProcessor> beanMap =
                beanFactory.getBeansOfType(BeanDefinitionRegistryPostProcessor.class, true, false);
        List<BeanDefinitionRegistryPostProcessor> registryPostProcessorBeans =
                new ArrayList<BeanDefinitionRegistryPostProcessor>(beanMap.values());
        OrderComparator.sort(registryPostProcessorBeans);
        for (BeanDefinitionRegistryPostProcessor postProcessor : registryPostProcessorBeans) {
            postProcessor.postProcessBeanDefinitionRegistry(registry);
        }
        invokeBeanFactoryPostProcessors(registryPostProcessors, beanFactory);
        invokeBeanFactoryPostProcessors(registryPostProcessorBeans, beanFactory);
        invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
        processedBeans.addAll(beanMap.keySet());
    }
    else {
        // Invoke factory processors registered with the context instance.
        invokeBeanFactoryPostProcessors(getBeanFactoryPostProcessors(), beanFactory);
    }

    // Do not initialize FactoryBeans here: We need to leave all regular beans
    // uninitialized to let the bean factory post-processors apply to them!
    String[] postProcessorNames =
        beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

    // Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
    // Ordered, and the rest.
    List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<BeanFactoryPostProcessor>();
    List<String> orderedPostProcessorNames = new ArrayList<String>();
    List<String> nonOrderedPostProcessorNames = new ArrayList<String>();
    for (String ppName : postProcessorNames) {
        if (processedBeans.contains(ppName)) {
            // skip - already processed in first phase above
        }
        else if (isTypeMatch(ppName, PriorityOrdered.class)) {
            priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
        }
        else if (isTypeMatch(ppName, Ordered.class)) {
            orderedPostProcessorNames.add(ppName);
        }
        else {
            nonOrderedPostProcessorNames.add(ppName);
        }
    }

    // First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
    OrderComparator.sort(priorityOrderedPostProcessors);
    invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

    // Next, invoke the BeanFactoryPostProcessors that implement Ordered.
    List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<BeanFactoryPostProcessor>();
    for (String postProcessorName : orderedPostProcessorNames) {
        orderedPostProcessors.add(getBean(postProcessorName, BeanFactoryPostProcessor.class));
    }
    OrderComparator.sort(orderedPostProcessors);
    invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

    // Finally, invoke all other BeanFactoryPostProcessors.
    List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<BeanFactoryPostProcessor>();
    for (String postProcessorName : nonOrderedPostProcessorNames) {
        nonOrderedPostProcessors.add(getBean(postProcessorName, BeanFactoryPostProcessor.class));
    }
    invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);
}
```

我们可以自己实现BeanFactoryPostProcessor接口并实现postProcessBeanFactory方法，**在所有Bean加载的流程开始前，会调用一次postProcessBeanFactory方法**。

接着40行~41行这两行，获取的是beanDefinitionMap中的Bean，即用户自定义的Bean。

接着第45行~61行，这里分出了三个List，表示开发者可以自定义BeanFactoryPostProcessor的调用顺序，具体为调用顺序为：

- **如果BeanFactoryPostProcessor实现了PriorityOrdered接口（PriorityOrdered接口是Ordered的子接口，没有自己的接口方法定义，只是做一个标记，表示调用优先级高于Ordered接口的子接口），是优先级最高的调用，调用顺序是按照接口方法getOrder()的实现，对返回的int值从小到大进行排序，进行调用**
- **如果BeanFactoryPostProcessor实现了Ordered接口，是优先级次高的调用，将在所有实现PriorityOrdered接口的BeanFactoryPostProcessor调用完毕之后，依据getOrder()的实现对返回的int值从小到大排序，进行调用**
- **不实现Ordered接口的BeanFactoryPostProcessor在上面的BeanFactoryPostProcessor调用全部完毕之后进行调用，调用顺序就是Bean定义的顺序**

最后的第63行~第80行就是按照上面的规则依次先将BeanFactoryPostProcessor接口对应的实现类实例化出来并调用postProcessBeanFactory方法。



### registerBeanPostProcessors方法

接下来看看registerBeanPostProcessors方法，顾名思义，就是注册自定义的BeanPostProcessor接口。看一下代码实现：

```java
protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

    // Register BeanPostProcessorChecker that logs an info message when
    // a bean is created during BeanPostProcessor instantiation, i.e. when
    // a bean is not eligible for getting processed by all BeanPostProcessors.
    int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
    beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

    // Separate between BeanPostProcessors that implement PriorityOrdered,
    // Ordered, and the rest.
    List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<BeanPostProcessor>();
    List<BeanPostProcessor> internalPostProcessors = new ArrayList<BeanPostProcessor>();
    List<String> orderedPostProcessorNames = new ArrayList<String>();
    List<String> nonOrderedPostProcessorNames = new ArrayList<String>();
    for (String ppName : postProcessorNames) {
        if (isTypeMatch(ppName, PriorityOrdered.class)) {
            BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
            priorityOrderedPostProcessors.add(pp);
            if (pp instanceof MergedBeanDefinitionPostProcessor) {
                internalPostProcessors.add(pp);
            }
        }
        else if (isTypeMatch(ppName, Ordered.class)) {
            orderedPostProcessorNames.add(ppName);
        }
        else {
            nonOrderedPostProcessorNames.add(ppName);
        }
    }

    // First, register the BeanPostProcessors that implement PriorityOrdered.
    OrderComparator.sort(priorityOrderedPostProcessors);
    registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

    // Next, register the BeanPostProcessors that implement Ordered.
    List<BeanPostProcessor> orderedPostProcessors = new ArrayList<BeanPostProcessor>();
    for (String ppName : orderedPostProcessorNames) {
        BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
        orderedPostProcessors.add(pp);
        if (pp instanceof MergedBeanDefinitionPostProcessor) {
            internalPostProcessors.add(pp);
        }
    }
    OrderComparator.sort(orderedPostProcessors);
    registerBeanPostProcessors(beanFactory, orderedPostProcessors);

    // Now, register all regular BeanPostProcessors.
    List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<BeanPostProcessor>();
    for (String ppName : nonOrderedPostProcessorNames) {
        BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
        nonOrderedPostProcessors.add(pp);
        if (pp instanceof MergedBeanDefinitionPostProcessor) {
            internalPostProcessors.add(pp);
        }
    }
    registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

    // Finally, re-register all internal BeanPostProcessors.
    OrderComparator.sort(internalPostProcessors);
    registerBeanPostProcessors(beanFactory, internalPostProcessors);

    beanFactory.addBeanPostProcessor(new ApplicationListenerDetector());
}
```



整体代码思路和invokeBeanFactoryPostProcessors方法类似，但是这里不会调用BeanPostProcessor接口的方法，而是把每一个BeanPostProcessor接口实现类实例化出来并按照顺序放入一个List中，到时候按顺序进行调用。

具体代码思路可以参考invokeBeanFactoryPostProcessors，这里就根据代码总结一下BeanPostProcessor接口的调用顺序：

- **优先调用PriorityOrdered接口的子接口，调用顺序依照接口方法getOrder的返回值从小到大排序**
- **其次调用Ordered接口的子接口，调用顺序依照接口方法getOrder的返回值从小到大排序**
- **接着按照BeanPostProcessor实现类在配置文件中定义的顺序进行调用**
- **最后调用MergedBeanDefinitionPostProcessor接口的实现Bean，同样按照在配置文件中定义的顺序进行调用**

### initMessageSource方法

initMessageSource方法用于初始化MessageSource，MessageSource是Spring定义的用于实现访问国际化的接口，看一下源码：

```java
protected void initMessageSource() {
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();
    if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
        this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
        // Make MessageSource aware of parent MessageSource.
        if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
            HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
            if (hms.getParentMessageSource() == null) {
                // Only set parent context as parent MessageSource if no parent MessageSource
                // registered already.
                hms.setParentMessageSource(getInternalParentMessageSource());
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Using MessageSource [" + this.messageSource + "]");
        }
    }
    else {
        // Use empty MessageSource to be able to accept getMessage calls.
        DelegatingMessageSource dms = new DelegatingMessageSource();
        dms.setParentMessageSource(getInternalParentMessageSource());
        this.messageSource = dms;
            beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
        if (logger.isDebugEnabled()) {
            logger.debug("Unable to locate MessageSource with name '" + MESSAGE_SOURCE_BEAN_NAME +
                    "': using default [" + this.messageSource + "]");
        }
    }
}


```

### initApplicationEventMulticaster方法

initApplicationEventMulticaster方法是用于初始化上下文事件广播器的，看一下源码：

```
protected void initApplicationEventMulticaster() {
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();
    if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
        this.applicationEventMulticaster =
                beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
        if (logger.isDebugEnabled()) {
            logger.debug("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
        }
    }
    else {
        this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
        if (logger.isDebugEnabled()) {
            logger.debug("Unable to locate ApplicationEventMulticaster with name '" +
                    APPLICATION_EVENT_MULTICASTER_BEAN_NAME +
                    "': using default [" + this.applicationEventMulticaster + "]");
        }
    }
}
```

和initMessageSource方法一样，这个if...else...判断也比较好理解：

- 如果自定义了名为"applicationEventMulticaster"的Bean，就实例化自定义的Bean，但自定义的Bean必须是ApplicationEventMulticaster接口的实现类
- 如果没有自定义名为"ApplicationEventMulticaster"的Bean，那么就注册一个类型为SimpleApplicationEventMulticaster的Bean

整个Spring的广播器是**观察者模式**的经典应用场景之一，这个之后有时间会分析Spring广播器的源码。

### onRefresh方法

接下来简单说说onRefresh方法，AbstractApplicationContext中这个方法没有什么定义：

```java
/**
 * Template method which can be overridden to add context-specific refresh work.
 * Called on initialization of special beans, before instantiation of singletons.
 * <p>This implementation is empty.
 * @throws BeansException in case of errors
 * @see #refresh()
 */
protected void onRefresh() throws BeansException {
    // For subclasses: do nothing by default.
}
```

看一下注释的意思：一个模板方法，重写它的作用是添加特殊上下文刷新的工作，在特殊Bean的初始化时、初始化之前被调用。在Spring中，AbstractRefreshableWebApplicationContext、GenericWebApplicationContext、StaticWebApplicationContext都实现了这个方法。



### registerListener方法

registerListeners方法顾名思义，用于注册监听器：

```java
/**
 * Add beans that implement ApplicationListener as listeners.
 * Doesn't affect other listeners, which can be added without being beans.
 */
protected void registerListeners() {
    // Register statically specified listeners first.
    for (ApplicationListener listener : getApplicationListeners()) {
        getApplicationEventMulticaster().addApplicationListener(listener);
    }
    // Do not initialize FactoryBeans here: We need to leave all regular beans
    // uninitialized to let post-processors apply to them!
    String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
    for (String lisName : listenerBeanNames) {
        getApplicationEventMulticaster().addApplicationListenerBean(lisName);
    }
}
```

### finishRefresh方法

最后一步，结束Spring上下文刷新：

```java
/**
 * Finish the refresh of this context, invoking the LifecycleProcessor's
 * onRefresh() method and publishing the
 * {@link org.springframework.context.event.ContextRefreshedEvent}.
 */
protected void finishRefresh() {
    // Initialize lifecycle processor for this context.
    initLifecycleProcessor();

    // Propagate refresh to lifecycle processor first.
    getLifecycleProcessor().onRefresh();

    // Publish the final event.
    publishEvent(new ContextRefreshedEvent(this));
}
```

这里面分了三步，第一步，初始化LifecycleProcessor接口：

```java
protected void initLifecycleProcessor() {
    ConfigurableListableBeanFactory beanFactory = getBeanFactory();
    if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
        this.lifecycleProcessor =
                beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
        if (logger.isDebugEnabled()) {
            logger.debug("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
        }
    }
    else {
        DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
        defaultProcessor.setBeanFactory(beanFactory);
        this.lifecycleProcessor = defaultProcessor;
        beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, this.lifecycleProcessor);
        if (logger.isDebugEnabled()) {
            logger.debug("Unable to locate LifecycleProcessor with name '" +
                    LIFECYCLE_PROCESSOR_BEAN_NAME +
                    "': using default [" + this.lifecycleProcessor + "]");
        }
    }
}
```

流程和initMessageSource方法、initApplicationEventMulticaster方法基本类似：

- 先找一下有没有自定义名为"lifecycleProcessor"的Bean，有的话就实例化出来，该Bean必须是LifecycleProcessor的实现类
- 没有自定义名为"lifecycleProcessor"的Bean，向Spring上下文中注册一个类型为DefaultLifecycleProcessor的LifecycleProcessor实现类

第二步，调用一下LifecycleProcessor的onRefresh方法。

第三步，由于之前已经初始化了

```java
public void publishEvent(ApplicationEvent event) {
    Assert.notNull(event, "Event must not be null");
    if (logger.isTraceEnabled()) {
        logger.trace("Publishing event in " + getDisplayName() + ": " + event);
    }
    getApplicationEventMulticaster().multicastEvent(event);
    if (this.parent != null) {
        this.parent.publishEvent(event);
    }
}
```

**后记**

再看AbstractApplicationContext的refresh方法，从中读到了很多细节：

- Spring默认加载的两个Bean，systemProperties和systemEnvironment，分别用于获取环境信息、系统信息
- BeanFactoryPostProcessor接口用于在所有Bean实例化之前调用一次postProcessBeanFactory
- 可以通过实现PriorityOrder、Order接口控制BeanFactoryPostProcessor调用顺序
- 可以通过实现PriorityOrder、Order接口控制BeanPostProcessor调用顺序
- 默认的MessageSource，名为"messageSource"
- 默认的ApplicationEventMulticaster，名为"applicationEventMulticaster"
- 默认的LifecycleProcessor，名为"lifecycleProcessor"