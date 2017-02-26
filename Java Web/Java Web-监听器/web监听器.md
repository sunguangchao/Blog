Web 监听器
-----------
定义：

* Servlet规范中定义的一种特殊类
* 用于监听 ServletContext、HttpSession和ServletRequest等域对象的创建与销毁事件
* 可以监听域对象的属性发生修改的事件
* 可以在事件发生前、发生后做一些必要的处理

用途：

* 统计在线人数和在线用户
* 系统启动时加载初始化信息
* 统计网站访问量
* 跟Spring结合

第一个监听器
---------
步骤

1. 创建一个实现监听器接口的类
2. 配置web.xml进行注册

监听器的启动顺序：

* 一个web.xml下的多个监听器，按照顺序启动
* 优先级：监听器 > 过滤器 > Servlet

监听器的分类
-------------
按照监听的对象划分：

* 用于监听应用程序环境对象(ServletContext)的事件监听器
* 用于监听用户会话对象(HttpSession)的事件监听器
* 用于监听请求消息对象(ServletRequest)的事件监听器

按照监听的事件划分：

* 监听域对象自身的创建和销毁的事件监听器
	* ServletContext(1)→ServletContextListener(N)
		* 用途：
			* 定时器
			* 全局属性变量
		* contextInitialized 方法
		* contextDestroyed 方法
	* HttpSession(1)→HttpSessionListener(N)
		* 用途：
			* 统计在线人数
			* 记录访问日志
		* sessionCreated 方法
		* sessionDestroyed 方法
	* ServletRequest(1)→ServletRequestListener(N)
		* 用途：
			* 读取参数
			* 记录访问历史
		* requestInitialized 方法
		* requestDestroyed 方法
* 监听域对象中的属性的增加和删除的事件监听器
* 监听绑定到HttpSession域中的某个对象的状态的事件监听器

监听绑定到HttpSession域中的某个对象的状态的事件监听器
-------------
Session钝化机制：

* 正常使用：服务器内存
* 不经常使用：序列化

本质在于把服务器中不经常使用的`Session`对象暂时序列化到系统文件系统或者是数据库系统中，当使用时反序列化到内存中，整个过程由服务器自动完成。

Tomcat中两种Session钝化管理器

* `org.apache.catalina.session.StandardManget`
	* 当Tomcat服务器被关闭或重启时，Tomcat服务器会将当前内存中的Session对象钝化到服务器系统文件中
	* 另一种情况是Web应用程序被重新加载时，内存中的Session对象也会被钝化到服务器的文件系统中
	* 钝化后的文件保存:Tomcat 安装路径`work/Catalina/hostname/applicationname/SESSION.ser`
* `org.apache.catalina.session.Persistentmanager`
	* 前两种情况和前面一致，第三种情况，可以配置主流内存的Session对象数目，将不长使用的Session对象保存到文件系统或数据库，当使用时再重新加载。
	* 默认情况下，Tomcat提供两个钝化驱动类
		* org.apache.Catalina.FileStore
		* org.apache.Catalina.JDBCStore
		
Servlet规范

* `HttpSessionBindlingListener`
	* 绑定：`valueBound`方法
	* 解除绑定：`valueUnbound`方法
* `HttpSessionActivationListener`
	* 钝化：`sessionWillPassive`方法
	* 活化：`sessionDidActivate`方法

注：不需要在web.xml中注册


