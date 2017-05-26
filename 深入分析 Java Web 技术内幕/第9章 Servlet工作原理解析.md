从Servlet容器说起
=============
Tomcat的容器分为4个等级，真正管理Servlet的容器是Context容器，一个Context对应一个Web工程。
Servlet容器的启动过程
--------------
```
Tomcat tomcat = getTomcatInstance();
File appDir = new File(getBuildDirectory(), "webapps/examples");
tomcat.addWebapp(null, "/examples", appDir.getAbsolutePath());
tomcat.start();
ByteChunk res = getUrl("http://localhost:" + getPort() + "/exampels/servlets/setvlet/HelloWorldExample");
assertTrue(res.toString().indexOf("<h1>Hello World!</h1>") > 0);
```
这段代码创建了一个Tomcat实例并新增了一个Web应用，然后启动Tomcat并调用其中的一个`HelloWorldExample Servlet`。
Tomcat的addWebapp方法的代码如下：
```
public Context addWebapp(Host host, String url, String path)
{
	silence(url);
	Context ctx = new StandardContext();
	ctx.setPath(url);
	ctx.setDocBase(path);
	if (defaultRealm == null) {
		initSimpleAuth();
	}
	ctx.setRealm(defaultRealm);
	ctx.addLifecycleListener(new DefaultWebXmlListener());
	ContextConfig ctxCfg = new ContextConfig();
	ctx.addLifecycleListener(ctxCfg);
	ctxCfg.setDefaultWebXml("org/apache/catalin/startup/NO_DEFAULT_XML");
	if (host == null) {
		getHost().addChild(ctx);
	}else{
		host.addChild(ctx);
	}
	return ctx;
}
```
其中`url`和`path`分别代表这个应用在Tomcat中的访问路径和实际的物理路径。重要的是`ContextConfig`，这个类会负责正整个Web应用配置的解析工作。

Tomcat的启动逻辑是基于观察者模式设计的。

`ContextConfig`的`init`方法主要完成以下工作：
* 创建用于解析XML配置文件的contextDigester对象
* 读取默认的context.xml配置文件，如果存在则解析它
* 读取默认的Host配置文件，如果存在则解析它
* 读取默认的Context自身的配置文件，如果存在则解析它
* 设置Context的DocBase

Web应用的初始化工作
----------
Web应用的初始化工作是在ContextConfig的configStart方法中实现的，应用的初始化主要是解析web.xml文件。

Tomcat首先找`globalWebXml`，然后找`hostWebXml`，接着寻找应用的配置文件`examples/WEB-INF/web.xml`。web.xml的各个配置项将会被解析成相应的属性保存在WebXml对象中。

下面是解析Servlet的代码片段：
```
P248
```
创建Servlet实例
=============
创建Servlet对象
-----------
创建Servlet实例的方法是从`Wrapper.loadServlet`开始的，loadServlet方法获取servletClass，然后交给InstanceManager去创建一个基于`servletClass.class`的对象。

初始化Servlet
-------------
调用Servlet的init()方法，同时把包装了StandWrapper传给Servlet。

Servlet体系结构
==========
Servlet的运行模式是典型的`握手型的交互式`运行模式，就是两个模块为了完成交换数据通常会准备一个交易场景。交易场景由ServleContext来描述，定制的参数由ServletConfig来描述。而ServletRequest和ServletResponse就是要交互的场景。

Servlet如何工作？
用户从浏览器向服务器发起一个请求通常会包含如下信息：`http://hostname:port/contextpath/servletpath`，hostname和port用来和服务器建立TCP连接，后面的URL才用来选择在服务器的哪个子容器服务用户的请求。
Tomcat7中有一个专门的类来做这件事：`org.apache.tomcat.util.http.mapper`，这个类保存了Tomcat的Container容器中所有子容器的信息，`org.apache.catalina.connector.Request`类进入Container容器之前，Mapper将会根据这次请求的hostname和contextpath将host和context容器设置到Request的`mappingData`属性中。

后面还有三校节
* Servlet中的Listener
* Filter如何工作
* Servlet中的url-pattern
