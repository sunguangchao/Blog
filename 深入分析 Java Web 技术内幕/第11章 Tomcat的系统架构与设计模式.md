Tomcat总体设计
==========
[解析Tomcat内部结构和请求过程 ](http://www.cnblogs.com/zhouyuqin/p/5143121.html)

Tomcat的核心有两个部件：Container和Connector，一个Container可以对应多个Connector然后就形成了Service，有了Service就可以提供对外服务。但是Service还要有一个生存环境，这个环境就是Server，所以整个Tomcat的生命周期由Server控制。

StandardService中setContainer方法：

```java
public void setContainer(Container container){
	Container oldContainer = this.container;
	if ((oldContainer != null) && (oldContainer instanceof Engine)) {
		oldContainer.setService(null);//去掉关联关系
	}
	this.container = container;
	if ((oldContainer != null) && (oldContainer instanceof Engine)) {
		oldContainer.setService(this);
	}
	if (started && (this.container != null) && (this.container instanceof Lifecycle)) {
		try{
			((Lifecycle)this.container).start();
		}catch(LifecycleException e){
			;
		}
	}
	synchronized(connectors){
		for (int i = 0; i < connectors.length; i++) {
			connectors[i].setContainer(this.container);
		}
	}
	if (started && (oldContainer != null) && (oldContainer instanceof Lifecycle)) {
		try{
			((Lifecycle)this.container).stop();
		}catch(LifecycleException e){
			;
		}
	}
	support.firePropertyChange("container", oldContainer, this.container);
}
```

Server提供一个接口让其他程序能够访问到这个Service集合，同时维护它包含的所有的Service的声明周期

Connector组件
--------
Connector组件的主要任务是负责接收浏览器发过来的TCP连接请求，创建一个Request和Response对象分别用于和请求端交换数据。Connector最重要的功能就是接收连接请求，然后分配线程让Container来处理这个请求。

HttpConnector的start方法：

```java
public void start() throws LifecycleException{
	if (started) {
		throw new LifecycleException(sm.getString("httpConnector.alreadyStarted"));
	}
	threadName = "HttpConnector[" + port + "]";
	lifecycle.fireLifecycleEvent(START_EVENT, null);
	started = null;
	threadStart();
	while(curProcessors < minProcessors){
		if ((maxProcessors > 0) && (curProcessors >= maxProcessors)) {
			break;
		}
		HttpProcessor processor = newProcessor();
		recycle(processor);
	}
}
```

当执行到threadStart()方法的时候，就会进入等待请求的状态，直到一个新的请求到来才会激活它继续执行。这个激活实在下面的方法中进行的：

```java
synchronized void assign(Socket socket){
	while(available){
		try{
			wait();
		}catch(InterruptedException e){
		}
	}
	this.socket = socket;
	available = true;
	notifyAll();
	if ((debug >= 1) && (socket != null)) {
		log(" An incoming request is being assigned");
	}
}
```



```java
private void process(Socket socket){
	boolean ok = true;
	boolean finishResponse = true;
	SocketInputStream input = null;
	OutputStream output = null;
	try{
		input = new SocketInputStream(socket.getInputStream(), connector.getBufferSize());
	}catch(Exception e){
		log("process.create", e);
		ok = false;
	}
	keepAlive = true;
	while(!stopped && ok && keepAlive){
		finishResponse = true;
		try{
			request.setStream(input);
			request.setResponse(response);
			output = socket.getOutputStream();
			response.setStream(output);
			response.setRequest(request);
			((HttpServletResponse) response.getResponse()).setHeader("Server", SERVER_INFO);
		}catch(Exception e){
			log("process.create", e);
			ok = false;
		}
		try{
			if (ok) {
				parseConnection(socket);
				parseRequest(input, output);
				if (!request.getRequset().getProtocol().startsWith("HTTP/0")) {
					parseHeaders(input);
				}
				if (httpl1) {
					ackRequest(output);
					if (connector.isChunkingAllowed()) {
						response.setAllowChunking(true);
					}
				}
				
			}
			try{
				if (ok) {
					connector.getContainer().invoke(requset, response);
				}

			}
			try{
				shutdowInput(input);
				socket.close();
			}catch(IOException e){
				;
			}catch(Throwable e){
				log("process.invoke", e);
			}
			socket = null;
		}
	}
}
```

