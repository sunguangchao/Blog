当Spring成功解析你定义的一个<bean/>节点后，在Spring的内部它就被转化成BeanDefinition对象，以后所有的操作都是对这个对象进行的。

Context组件
============
Context作为Spring的IOC容器，基本上整合了Spring的大部分功能

ApplicationContext的任务：
1. 标识一个应用环境
2. 利用BeanFactory创建Bean对象
3. 保存对象关系表
4. 能够捕获各种事件

Core组件
=========

如何创建BeanFactory工厂

```java
public void refresh() throws BeansException, IllegalStateException(){
​    synchronized(this.startupShutdownMonitor){
​        prepareRefresh();
​        ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
​        prepareBeanFactory(beanFactory);
​        try{
​            //注册实现了BeanPostProcessor接口的bean
​            postProcessBeanFactory(beanFactory);
​            //初始化和执行BeanFactoryPostProcessor beans
​            invokeBeanFactoryPostProcessors(beanFactory);
​            //初始化和执行BeanPostProcessor beans
​            registerBeanPostProcessors(beanFactory);
​            //初始化MessageSource
​            initMessageSource();
​            //初始化 event multicaster
​            initAppliactionEventMulticaster();
​            //刷新由子类实现的方法
​            onRefresh();
​            //检查注册时事件
​            registerListeners();
​            //初始化non-lazy-init单例bean
​            finishBeanFactoryInitialization(beanFactory);
​            //执行LifecycleProcessor.onRefresh()和ContextRefreshed Event事件
​            finishRefresh();
​        }catch(BeansException ex){
​            destroyBeans();
​            cancelRefresh();
​            throw ex;
​        }
​    }
}
```

1. 构建BeanFactory，以便于产生所需的“演员”
2. 注册可能感兴趣的事件
3. 创建Bean实例对象
4. 触发被监听的事件

首先创建和配置BeanFactory，这里是refresh，也就是刷新配置

```java

protected final void refreshBeanFactory() throws BeansException{

​    if(hasBeanFactory()){

​        destroyBeans();

​        closeBeanFactory();

​    }

​    try{

​        DefaultListableBeanFactory beanFactory = createBeanFactory();

​        beanFactory.setSerializationId(getId());

​        customizeBeanFactory(beanFactory);

​        loadBeanDefinitions(beanFactory);

​        synchronized(this.beanFactoryMonitor){

​            this.beanFactory = beanFactory;

​        }

​    }catch(IOException ex){

​        throw new ApplicationContextException("I/O error parsing 

​            bean definition source for " + getDisplayName(), ex);

​    }

}
```

如何创建Bean实例并创建Bean的关系网
----------------

Bean的实例化代码，从finishBeanFactoryInitialization开始

```java
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory){

    beanFactory.setTempClassLoader(null);

    //禁止修改当前Bean的配置信息

    beanFactory.freezeConfiguration();

    //实例化non-lazy-init类型的bean

    beanFactory.preInstantiateSingletons();

}
```

```java

public void PreInstantiateSingletons() throws BeansException(){

​    if (this.logger.isInfoEnabled()) {

​        this.logger.info("Pre-instantiating singleton is " + this);

​    }

​    synchronized(this.beanDefinitionMap){

​        for (String beanName : this.beanDefinitionNames) {

​            if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {

​                if (isFactoryBean(beanName)) {

​                    final FactoryBean factory = (FactoryBean)getBean(FACTORY_BEAN_PREFIX + beanName);

​                    boolean isEagerInit;

​                    if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {

​                        isEagerInit = AccessController.doPrivileged(new PrivilegedAction<Boolean>(){

​                            public Boolean run(){

​                                return ((SmartFactoryBean) factory).isEagerInit();

​                            }

​                        }, getAccessControlContext());

​                    }else{

​                        isEagerInit = factory instanceof SmartFactoryBean

​                        ((SmartFactoryBean) factory).isEagerInit();

​                    }

​                    if (isEagerInit) {

​                        getBean(beanName);

​                    }

​                }else{

​                    getBean(beanName);

​                }

​            }

​        }

​    }

}

```

ApplicationContext.xml就是IOC容器的默认配置文件

Spring中AOP的特性详解