```java
// Create bean instance.
if (mbd.isSingleton()) {
	sharedInstance = getSingleton(beanName, new ObjectFactory<Object>() {
		@Override
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
//原型类的bean，每次获取的时候
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
		Object scopedInstance = scope.get(beanName, new ObjectFactory<Object>() {
			@Override
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
```

### 通过FactoryBean获取Bean实例源码实现

我们知道可以通过实现FactoryBean接口，重写getObject()方法实现个性化定制Bean的过程，这部分我们就来看一下Spring源码是如何实现通过FactoryBean获取Bean实例的。代码直接定位到AbstractBeanFactory的doGetBean方法创建单例Bean这部分：

```java
protected Object getObjectForBeanInstance(
        Object beanInstance, String name, String beanName, RootBeanDefinition mbd) {

    // Don't let calling code try to dereference the factory if the bean isn't a factory.
  	//判断一下是否beanName以"&"开头并且不是FactoryBean的实现类，不满足则抛异常，因为beanName以"&"开头是FactoryBean的实现类bean定义的一个特征。
    if (BeanFactoryUtils.isFactoryDereference(name) && !(beanInstance instanceof FactoryBean)) {
        throw new BeanIsNotAFactoryException(transformedBeanName(name), beanInstance.getClass());
    }

    // Now we have the bean instance, which may be a normal bean or a FactoryBean.
    // If it's a FactoryBean, we use it to create a bean instance, unless the
    // caller actually wants a reference to the factory.
  	// 如果bean不是FactoryBean的实现类
  	// 或者beanName以"&"开头。都直接把生成的bean对象返回出去，不会执行余下的流程。
    if (!(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)) {
        return beanInstance;
    }

    Object object = null;
    if (mbd == null) {
        object = getCachedObjectForFactoryBean(beanName);
    }
    if (object == null) {
        // Return bean instance from factory.
        FactoryBean factory = (FactoryBean) beanInstance;
        // Caches object obtained from FactoryBean if it is a singleton.
        if (mbd == null && containsBeanDefinition(beanName)) {
            mbd = getMergedLocalBeanDefinition(beanName);
        }
        boolean synthetic = (mbd != null && mbd.isSynthetic());
        object = getObjectFromFactoryBean(factory, beanName, !synthetic);
    }
    return object;
}
```

首先第5行~第7行判断一下是否beanName以"&"开头并且不是FactoryBean的实现类，不满足则抛异常，因为beanName以"&"开头是FactoryBean的实现类bean定义的一个特征。

```java
protected Object getObjectFromFactoryBean(FactoryBean factory, String beanName, boolean shouldPostProcess) {
    if (factory.isSingleton() && containsSingleton(beanName)) {
        synchronized (getSingletonMutex()) {
            Object object = this.factoryBeanObjectCache.get(beanName);
            if (object == null) {
                object = doGetObjectFromFactoryBean(factory, beanName, shouldPostProcess);
                this.factoryBeanObjectCache.put(beanName, (object != null ? object : NULL_OBJECT));
            }
            return (object != NULL_OBJECT ? object : null);
        }
    }
    else {
        return doGetObjectFromFactoryBean(factory, beanName, shouldPostProcess);
    }
}
```

```java
private Object doGetObjectFromFactoryBean(
        final FactoryBean factory, final String beanName, final boolean shouldPostProcess)
        throws BeanCreationException {

    Object object;
    try {
        if (System.getSecurityManager() != null) {
            AccessControlContext acc = getAccessControlContext();
            try {
                object = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                    public Object run() throws Exception {
                            return factory.getObject();
                        }
                    }, acc);
            }
            catch (PrivilegedActionException pae) {
                throw pae.getException();
            }
        }
        else {
            object = factory.getObject();
        }
    }
    catch (FactoryBeanNotInitializedException ex) {
        throw new BeanCurrentlyInCreationException(beanName, ex.toString());
    }
    catch (Throwable ex) {
        throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
    }
        
    // Do not accept a null value for a FactoryBean that's not fully
    // initialized yet: Many FactoryBeans just return null then.
    if (object == null && isSingletonCurrentlyInCreation(beanName)) {
        throw new BeanCurrentlyInCreationException(
                beanName, "FactoryBean which is currently in creation returned null from getObject");
    }

    if (object != null && shouldPostProcess) {
        try {
            object = postProcessObjectFromFactoryBean(object, beanName);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Post-processing of the FactoryBean's object failed", ex);
        }
    }

    return object;
}
```

第12行和第21行的代码，都一样，最终调用getObject()方法获取对象。回过头去看之前的getObjectFromFactoryBean方法，虽然if...else...逻辑最终都是调用了以上的方法，但是区别在于：

- **如果FactoryBean接口实现类的isSington方法返回的是true，那么每次调用getObject方法的时候会优先尝试从FactoryBean对象缓存中取目标对象，有就直接拿，没有就创建并放入FactoryBean对象缓存，这样保证了每次单例的FactoryBean调用getObject()方法后最终拿到的目标对象一定是单例的，即在内存中都是同一份**
- **如果FactoryBean接口实现类的isSington方法返回的是false，那么每次调用getObject方法的时候都会新创建一个目标对象**

 