上文比较详细地分析了Spring上下文加载的代码入口，并且在AbstractApplicationContext的refresh方法中，点出了finishBeanFactoryInitialization方法完成了对于所有非懒加载的Bean的初始化。

finishBeanFactoryInitialization方法中调用了DefaultListableBeanFactory的preInstantiateSingletons方法，本文针对preInstantiateSingletons进行分析，解读一下Spring是如何初始化Bean实例对象出来的。



```java
	/**
	 * AbstractApplicationContext.class
	 * Finish the initialization of this context's bean factory,
	 * initializing all remaining singleton beans.
	 */
	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
		// Initialize conversion service for this context.
		if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
				beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
			beanFactory.setConversionService(
					beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
		}

		// Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
		String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
		for (String weaverAwareName : weaverAwareNames) {
			getBean(weaverAwareName);
		}

		// Stop using the temporary ClassLoader for type matching.
		beanFactory.setTempClassLoader(null);

		// Allow for caching all bean definition metadata, not expecting further changes.
		beanFactory.freezeConfiguration();

		// Instantiate all remaining (non-lazy-init) singletons.
		beanFactory.preInstantiateSingletons();
	}
```



### DefaultListableBeanFactory的preInstantiateSingletons()方法

```java
public void preInstantiateSingletons() throws BeansException {
    if (this.logger.isInfoEnabled()) {
        this.logger.info("Pre-instantiating singletons in " + this);
    }
    synchronized (this.beanDefinitionMap) {
        // Iterate over a copy to allow for init methods which in turn register new bean definitions.
        // While this may not be part of the regular factory bootstrap, it does otherwise work fine.
        List<String> beanNames = new ArrayList<String>(this.beanDefinitionNames);
        for (String beanName : beanNames) {
            RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
            if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
                if (isFactoryBean(beanName)) {
                    final FactoryBean factory = (FactoryBean) getBean(FACTORY_BEAN_PREFIX + beanName);
                    boolean isEagerInit;
                    if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
                        isEagerInit = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                            public Boolean run() {
                                return ((SmartFactoryBean) factory).isEagerInit();
                            }
                        }, getAccessControlContext());
                    }
                    else {
                        isEagerInit = (factory instanceof SmartFactoryBean &&
                                ((SmartFactoryBean) factory).isEagerInit());
                    }
                    if (isEagerInit) {
                        getBean(beanName);
                    }
                }
                else {
                    getBean(beanName);
                }
            }
        }
    }
}
```

这里先解释一下getMergedLocalBeanDefinition方法的含义，因为这个方法会常常看到。**Bean定义公共的抽象类是AbstractBeanDefinition，普通的Bean在Spring加载Bean定义的时候，实例化出来的是GenericBeanDefinition，而Spring上下文包括实例化所有Bean用的AbstractBeanDefinition是RootBeanDefinition，这时候就使用getMergedLocalBeanDefinition方法做了一次转化，将非RootBeanDefinition转换为RootBeanDefinition以供后续操作**。



解释完了getMergedLocalBeanDefinition方法的作用，第1行~第10行的代码就没什么好说的了，根据beanName拿到RootBeanDefinition而已。由于此方法实例化的是所有非懒加载的单例Bean，因此要实例化Bean，必须满足11行的三个定义：

（1）不是抽象的

（2）必须是单例的

（3）必须是非懒加载的

接着简单看一下第12行~第29行的代码，这段代码主要做的是一件事情：首先判断一下Bean是否FactoryBean的实现，接着判断Bean是否SmartFactoryBean的实现，假如Bean是SmartFactoryBean的实现并且eagerInit（这个单词字面意思是渴望加载，找不到一个好的词语去翻译，意思就是定义了这个Bean需要立即加载的意思）的话，会立即实例化这个Bean。Java开发人员不需要关注这段代码，因为SmartFactoryBean基本不会用到，我翻译一下Spring官网对于SmartFactoryBean的定义描述：

- FactoryBean接口的扩展接口。接口实现并不表示是否总是返回单独的实例对象，比如FactoryBean.isSingleton()实现返回false的情况并不清晰地表示每次返回的都是单独的实例对象
- 不实现这个扩展接口的简单FactoryBean的实现，FactoryBean.isSingleton()实现返回false总是简单地告诉我们每次返回的都是单独的实例对象，暴露出来的对象只能够通过命令访问
- 注意：**这个接口是一个有特殊用途的接口，主要用于框架内部使用与Spring相关**。通常，应用提供的FactoryBean接口实现应当只需要实现简单的FactoryBean接口即可，新方法应当加入到扩展接口中去



### doGetBean方法构造Bean流程

上面把getBean之外的代码都分析了一下，看代码就可以知道，获取Bean对象实例，都是通过getBean方法，getBean方法最终调用的是DefaultListableBeanFactory的父类AbstractBeanFactory类的doGetBean方法，因此这部分重点分析一下doGetBean方法是如何构造出一个单例的Bean的。



```java
protected <T> T doGetBean(
        final String name, final Class<T> requiredType, final Object[] args, boolean typeCheckOnly)
        throws BeansException {

    final String beanName = transformedBeanName(name);
    Object bean;

    // Eagerly check singleton cache for manually registered singletons.
    //首先检查一下本地的单例缓存是否已经加载过Bean，没有的话再检查earlySingleton缓存是否已经加载过Bean（又是early，不好找到词语翻译），没有的话执行后面的逻辑。
    Object sharedInstance = getSingleton(beanName);
    if (sharedInstance != null && args == null) {
        if (logger.isDebugEnabled()) {
            if (isSingletonCurrentlyInCreation(beanName)) {
                logger.debug("Returning eagerly cached instance of singleton bean '" + beanName +
                        "' that is not fully initialized yet - a consequence of a circular reference");
            }
            else {
                logger.debug("Returning cached instance of singleton bean '" + beanName + "'");
            }
        }
        bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
    }

    else {
        // Fail if we're already creating this bean instance:
        // We're assumably within a circular reference.
        // 这里执行的都是一些基本的检查和简单的操作，包括bean是否是prototype的（prototype的Bean当前创建会抛出异常）、是否抽象的、将beanName加入alreadyCreated这个Set中等。
        if (isPrototypeCurrentlyInCreation(beanName)) {
            throw new BeanCurrentlyInCreationException(beanName);
        }

        // Check if bean definition exists in this factory.
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            // Not found -> check parent.
            String nameToLookup = originalBeanName(name);
            if (args != null) {
                // Delegation to parent with explicit args.
                return (T) parentBeanFactory.getBean(nameToLookup, args);
            }
            else {
                // No args -> delegate to standard getBean method.
                return parentBeanFactory.getBean(nameToLookup, requiredType);
            }
        }

        if (!typeCheckOnly) {
            markBeanAsCreated(beanName);
        }

        final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        checkMergedBeanDefinition(mbd, beanName, args);

        // Guarantee initialization of beans that the current bean depends on.
        //我们经常在bean标签中看到depends-on这个属性，就是通过这段保证了depends-on依赖的Bean会优先于当前Bean被加载。
        String[] dependsOn = mbd.getDependsOn();
        if (dependsOn != null) {
            for (String dependsOnBean : dependsOn) {
                getBean(dependsOnBean);
                registerDependentBean(dependsOnBean, beanName);
            }
        }

        // Create bean instance.
        if (mbd.isSingleton()) {
            sharedInstance = getSingleton(beanName, new ObjectFactory() {
                public Object getObject() throws BeansException {
                    try {
                        return createBean(beanName, mbd, args);
                    }
                    catch (BeansException ex) {
                        // Explicitly remove instance from singleton cache: It might have been put there
                        // eagerly by the creation process, to allow for circular reference resolution.
                        // Also remove any beans that received a temporary reference to the bean.
                        destroySingleton(beanName);
                        throw ex;
                    }
                }
            });
            bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
        }

        else if (mbd.isPrototype()) {
            // It's a prototype -> create a new instance.
            Object prototypeInstance = null;
            try {
                beforePrototypeCreation(beanName);
                prototypeInstance = createBean(beanName, mbd, args);
            }
            finally {
                afterPrototypeCreation(beanName);
            }
            bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
        }

        else {
            String scopeName = mbd.getScope();
            final Scope scope = this.scopes.get(scopeName);
            if (scope == null) {
                throw new IllegalStateException("No Scope registered for scope '" + scopeName + "'");
            }
            try {
                Object scopedInstance = scope.get(beanName, new ObjectFactory() {
                    public Object getObject() throws BeansException {
                            beforePrototypeCreation(beanName);
                        try {
                            return createBean(beanName, mbd, args);
                        }
                        finally {
                            afterPrototypeCreation(beanName);
                        }
                    }
                });
                bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);
            }
            catch (IllegalStateException ex) {
                throw new BeanCreationException(beanName,
                        "Scope '" + scopeName + "' is not active for the current thread; " +
                        "consider defining a scoped proxy for this bean if you intend to refer to it from a singleton",
                        ex);
            }
        }
    }

    // Check if required type matches the type of the actual bean instance.
    if (requiredType != null && bean != null && !requiredType.isAssignableFrom(bean.getClass())) {
        try {
            return getTypeConverter().convertIfNecessary(bean, requiredType);
        }
        catch (TypeMismatchException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to convert bean '" + name + "' to required type [" +
                        ClassUtils.getQualifiedName(requiredType) + "]", ex);
            }
            throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
        }
    }
    return (T) bean;
}
```





getSingleton方法不贴了，有一些前置的判断，很简单的逻辑，重点就是调用了ObjectFactory的getObject()方法来获取到单例Bean对象，方法的实现是调用了createBean方法，createBean方法是AbstractBeanFactory的子类AbstractAutowireCapableBeanFactory的一个方法，看一下它的方法实现：

```java
protected Object createBean(final String beanName, final RootBeanDefinition mbd, final Object[] args)
        throws BeanCreationException {

    if (logger.isDebugEnabled()) {
        logger.debug("Creating instance of bean '" + beanName + "'");
    }
    // Make sure bean class is actually resolved at this point.
    resolveBeanClass(mbd, beanName);

    // Prepare method overrides.
    try {
        mbd.prepareMethodOverrides();
    }
    catch (BeanDefinitionValidationException ex) {
        throw new BeanDefinitionStoreException(mbd.getResourceDescription(),
                beanName, "Validation of method overrides failed", ex);
    }

    try {
        // Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
        Object bean = resolveBeforeInstantiation(beanName, mbd);
        if (bean != null) {
            return bean;
        }
    }
    catch (Throwable ex) {
        throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                "BeanPostProcessor before instantiation of bean failed", ex);
    }

    Object beanInstance = doCreateBean(beanName, mbd, args);
    if (logger.isDebugEnabled()) {
        logger.debug("Finished creating instance of bean '" + beanName + "'");
    }
    return beanInstance;
}
```

核心是doCreateBean这个方法

```java
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final Object[] args) {
    // Instantiate the bean.
    BeanWrapper instanceWrapper = null;
    if (mbd.isSingleton()) {
        instanceWrapper = this.factoryBeanInstanceCache.remove(beanName);
    }
    if (instanceWrapper == null) {
        instanceWrapper = createBeanInstance(beanName, mbd, args);
    }
    final Object bean = (instanceWrapper != null ? instanceWrapper.getWrappedInstance() : null);
    Class beanType = (instanceWrapper != null ? instanceWrapper.getWrappedClass() : null);

    // Allow post-processors to modify the merged bean definition.
    synchronized (mbd.postProcessingLock) {
        if (!mbd.postProcessed) {
            applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
            mbd.postProcessed = true;
        }
    }

    // Eagerly cache singletons to be able to resolve circular references
    // even when triggered by lifecycle interfaces like BeanFactoryAware.
    boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
            isSingletonCurrentlyInCreation(beanName));
    if (earlySingletonExposure) {
        if (logger.isDebugEnabled()) {
            logger.debug("Eagerly caching bean '" + beanName +
                    "' to allow for resolving potential circular references");
        }
        addSingletonFactory(beanName, new ObjectFactory() {
            public Object getObject() throws BeansException {
                return getEarlyBeanReference(beanName, mbd, bean);
            }
        });
    }

    // Initialize the bean instance.
    Object exposedObject = bean;
    try {
        populateBean(beanName, mbd, instanceWrapper);
        if (exposedObject != null) {
            exposedObject = initializeBean(beanName, exposedObject, mbd);
        }
    }
    catch (Throwable ex) {
        if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
            throw (BeanCreationException) ex;
        }
        else {
            throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
        }
    }

    if (earlySingletonExposure) {
        Object earlySingletonReference = getSingleton(beanName, false);
        if (earlySingletonReference != null) {
            if (exposedObject == bean) {
                exposedObject = earlySingletonReference;
            }
            else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
                String[] dependentBeans = getDependentBeans(beanName);
                Set<String> actualDependentBeans = new LinkedHashSet<String>(dependentBeans.length);
                for (String dependentBean : dependentBeans) {
                    if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
                        actualDependentBeans.add(dependentBean);
                    }
                }
                if (!actualDependentBeans.isEmpty()) {
                    throw new BeanCurrentlyInCreationException(beanName,
                            "Bean with name '" + beanName + "' has been injected into other beans [" +
                                StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
                            "] in its raw version as part of a circular reference, but has eventually been " +
                            "wrapped. This means that said other beans do not use the final version of the " +
                            "bean. This is often the result of over-eager type matching - consider using " +
                            "'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
                }
            }
        }
    }

    // Register bean as disposable.
    try {
        registerDisposableBeanIfNecessary(beanName, bean, mbd);
    }
    catch (BeanDefinitionValidationException ex) {
        throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
    }

    return exposedObject;
}
```



### 创建Bean实例

第8行的`createBeanInstance`方法，会创建出Bean的实例，并包装为`BeanWrapper`，看一下`createBeanInstance`方法，只贴最后一段比较关键的：

```java
 1 // Need to determine the constructor...
 2 Constructor[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
 3 if (ctors != null ||
 4         mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR ||
 5         mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args))  {
 6     return autowireConstructor(beanName, mbd, ctors, args);
 7 }
 8 
 9 // No special handling: simply use no-arg constructor.
10 return instantiateBean(beanName, mbd);
```

意思是bean标签使用构造函数注入属性的话，执行第6行，否则执行第10行。MultiFunctionBean使用默认构造函数，使用setter注入属性，因此执行第10行代码：

```java
 1 protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
 2     try {
 3         Object beanInstance;
 4         final BeanFactory parent = this;
 5         if (System.getSecurityManager() != null) {
 6             beanInstance = AccessController.doPrivileged(new PrivilegedAction<Object>() {
 7                 public Object run() {
 8                     return getInstantiationStrategy().instantiate(mbd, beanName, parent);
 9                 }
10             }, getAccessControlContext());
11         }
12         else {
13             beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);
14         }
15         BeanWrapper bw = new BeanWrapperImpl(beanInstance);
16         initBeanWrapper(bw);
17         return bw;
18     }
19     catch (Throwable ex) {
20         throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
21     }
22 }
```

代码执行到13行：

```java
 1 public Object instantiate(RootBeanDefinition beanDefinition, String beanName, BeanFactory owner) {
 2     // Don't override the class with CGLIB if no overrides.
 3     if (beanDefinition.getMethodOverrides().isEmpty()) {
 4         Constructor<?> constructorToUse;
 5         synchronized (beanDefinition.constructorArgumentLock) {
 6             constructorToUse = (Constructor<?>) beanDefinition.resolvedConstructorOrFactoryMethod;
 7             if (constructorToUse == null) {
 8                 final Class clazz = beanDefinition.getBeanClass();
 9                 if (clazz.isInterface()) {
10                     throw new BeanInstantiationException(clazz, "Specified class is an interface");
11                 }
12                 try {
13                     if (System.getSecurityManager() != null) {
14                         constructorToUse = AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor>() {
15                             public Constructor run() throws Exception {
16                                 return clazz.getDeclaredConstructor((Class[]) null);
17                             }
18                         });
19                     }
20                     else {
21                         constructorToUse = clazz.getDeclaredConstructor((Class[]) null);
22                     }
23                     beanDefinition.resolvedConstructorOrFactoryMethod = constructorToUse;
24                 }
25                 catch (Exception ex) {
26                     throw new BeanInstantiationException(clazz, "No default constructor found", ex);
27                 }
28             }
29         }
30         return BeanUtils.instantiateClass(constructorToUse);
31     }
32     else {
33         // Must generate CGLIB subclass.
34         return instantiateWithMethodInjection(beanDefinition, beanName, owner);
35     }
36 }
```
整段代码都在做一件事情，就是选择一个使用的构造函数。当然第9行顺带做了一个判断：实例化一个接口将报错。

最后调用到30行，看一下代码：

```java
 1 public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
 2     Assert.notNull(ctor, "Constructor must not be null");
 3     try {
 4         ReflectionUtils.makeAccessible(ctor);
 5         return ctor.newInstance(args);
 6     }
 7     catch (InstantiationException ex) {
 8         throw new BeanInstantiationException(ctor.getDeclaringClass(),
 9                 "Is it an abstract class?", ex);
10     }
11     catch (IllegalAccessException ex) {
12         throw new BeanInstantiationException(ctor.getDeclaringClass(),
13                 "Is the constructor accessible?", ex);
14     }
15     catch (IllegalArgumentException ex) {
16         throw new BeanInstantiationException(ctor.getDeclaringClass(),
17                 "Illegal arguments for constructor", ex);
18     }
19     catch (InvocationTargetException ex) {
20         throw new BeanInstantiationException(ctor.getDeclaringClass(),
21                 "Constructor threw exception", ex.getTargetException());
22     }
23 }
```
通过反射生成Bean的实例。看到前面有一步makeAccessible，这意味着**即使Bean的构造函数是private、protected的，依然不影响Bean的构造**。

最后注意一下，这里被实例化出来的Bean并不会直接返回，而是会被包装为**BeanWrapper**继续在后面使用。

 