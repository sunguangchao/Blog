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
```
