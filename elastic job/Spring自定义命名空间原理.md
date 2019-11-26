Spring自定义命名空间原理

META_INF下面自定义reg.xsd文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsd:schema xmlns="http://www.dangdang.com/schema/ddframe/reg"
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        xmlns:beans="http://www.springframework.org/schema/beans"
        targetNamespace="http://www.dangdang.com/schema/ddframe/reg"
        elementFormDefault="qualified"
        attributeFormDefault="unqualified">
    <xsd:import namespace="http://www.springframework.org/schema/beans"/>
    
    <xsd:element name="zookeeper">
        <xsd:complexType>
            <xsd:complexContent>
                <xsd:extension base="beans:identifiedType">
                    <xsd:attribute name="server-lists" type="xsd:string" use="required" />
                    <xsd:attribute name="namespace" type="xsd:string" use="required" />
                    <xsd:attribute name="base-sleep-time-milliseconds" type="xsd:string" />
                    <xsd:attribute name="max-sleep-time-milliseconds" type="xsd:string" />
                    <xsd:attribute name="max-retries" type="xsd:string" />
                    <xsd:attribute name="session-timeout-milliseconds" type="xsd:string" />
                    <xsd:attribute name="connection-timeout-milliseconds" type="xsd:string" />
                    <xsd:attribute name="digest" type="xsd:string" />
                </xsd:extension>
            </xsd:complexContent>
        </xsd:complexType>
    </xsd:element>
</xsd:schema>
```

xsd:schema元素详解：

* `xmlns="http://www.dangdang.com/schema/ddframe/reg"`定义默认命名空间
* `xmlns:xsd="http://www.w3.org/2001/XMLSchema"`引入xsd命名空间，该命名空间的URL为http://www.w3.org/2001/XMLSchema，元素前缀为xsd。 
* `xmlns:beans="http://www.springframework.org/schema/beans"`引入Spring beans命名空间
* `targetNamespace="http://www.dangdang.com/schema/ddframe/reg"`定义该命名空间对应的url，？
* `elementFormDefault="qualified"`指定该xsd所对应的实例xml文件，引用该文件中定义的元素必须被命名空间所限定。例如在reg.xsd中定义了zookeeper这个元素，那么在regContext.xml中使用该元素来定义时，必须这样写： `<reg:zookeeper id="regCenter" reg:server-lists="" .../>`

* `<xsd:import namespace="http://www.springframework.org/schema/beans"/>`导入其他命名空间，这里表示导入spring beans命名空间
* `<xsd:element name="zookeeper">`定义zookeeper元素，xml文件中可以使用reg:zookeeper
* `<xsd:complexType>`，zookeeper元素的类型为复杂类型
* `<xsd:extension base="beans:identifiedType">`继承beans命名空间identifiedType的属性





下一步，继承NamespaceHandlerSupport类，重写init()方法

```java
/**
 * 注册中心的命名空间处理器.
 * 
 * @author zhangliang
 */
public final class RegNamespaceHandler extends NamespaceHandlerSupport {
    
    @Override
    public void init() {
        registerBeanDefinitionParser("zookeeper", new ZookeeperBeanDefinitionParser());
    }
}

```

注册BeanDefinitionParser解析`<reg:zookeeper/>`标签，并初始化实例。 

```java
public final class ZookeeperBeanDefinitionParser extends AbstractBeanDefinitionParser {
    public ZookeeperBeanDefinitionParser() {
    }

    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        //构建器模式，表明标签对应的实体Bean对象为ZookeeperRegistryCenter,zk注册中心实现类。
        BeanDefinitionBuilder result = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperRegistryCenter.class);
        //最终创建ZookeeperRegistryCenter，其属性通过构造方法注入。 
        result.addConstructorArgValue(this.buildZookeeperConfigurationBeanDefinition(element));
        //设置initMethod，相当于配置文件的init-method属性，表明在创建实例时将调用该方法进行初始化。
        result.setInitMethodName("init");
        //返回AbstractBeanDefinition对象，方便Spring针对该配置创建实例。 
        return result.getBeanDefinition();
    }

    /**
     * 根据<reg:zookeeper/>元素，获取element的server-lists、namespace属性，
     * 使用ZookeeperConfiguration构造方式初始化ZookeeperConfiguration属性，
     * 然后解析其他非空属性并使用set方法注入到ZookeeperConfiguration实例。 
     * @param element
     * @return
     */
    private AbstractBeanDefinition buildZookeeperConfigurationBeanDefinition(Element element) {
        BeanDefinitionBuilder configuration = BeanDefinitionBuilder.rootBeanDefinition(ZookeeperConfiguration.class);
        configuration.addConstructorArgValue(element.getAttribute("server-lists"));
        configuration.addConstructorArgValue(element.getAttribute("namespace"));
        this.addPropertyValueIfNotEmpty("base-sleep-time-milliseconds", "baseSleepTimeMilliseconds", element, configuration);
        this.addPropertyValueIfNotEmpty("max-sleep-time-milliseconds", "maxSleepTimeMilliseconds", element, configuration);
        this.addPropertyValueIfNotEmpty("max-retries", "maxRetries", element, configuration);
        this.addPropertyValueIfNotEmpty("session-timeout-milliseconds", "sessionTimeoutMilliseconds", element, configuration);
        this.addPropertyValueIfNotEmpty("connection-timeout-milliseconds", "connectionTimeoutMilliseconds", element, configuration);
        this.addPropertyValueIfNotEmpty("digest", "digest", element, configuration);
        return configuration.getBeanDefinition();
    }

    private void addPropertyValueIfNotEmpty(String attributeName, String propertyName, Element element, BeanDefinitionBuilder factory) {
        String attributeValue = element.getAttribute(attributeName);
        if (!Strings.isNullOrEmpty(attributeValue)) {
            factory.addPropertyValue(propertyName, attributeValue);
        }

    }
}
```



将自定义的NameSpace、xsd文件纳入Spring的管理范围内
--------------

在META-INF目录下创建spring.handlers、spring.schemas文件，其内容分别是： 

```xml
http\://www.dangdang.com/schema/ddframe/reg=io.elasticjob.lite.spring.reg.handler.RegNamespaceHandler
```

格式如下：xsd文件中定义的targetNamespace=自定义namespace实现类。

```xml
http\://www.dangdang.com/schema/ddframe/reg/reg.xsd=META-INF/namespace/reg.xsd
```

格式如下：xsd文件uri = xsd文件目录。

