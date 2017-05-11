什么是session?
--------------

* session表示客户端与服务器的一次会话
* 具体到Web中的 Session 指的就是用户在浏览某个网站时，从进入网站到浏览器关闭所经过的这段时间，也就是用户浏览这个网站所花费的时间。
* session实际上是一个特定的时间概念

在服务器的内存中保存着不同用户的 session。

session对象
--------------

* `session`对象是一个 JSP 内置对象
* `session`对象在第一个 JSP 页面被装载时自动创建，完成会话期管理。
* 从一个客户打开浏览器并连接到服务器开始，到客户关闭浏览器离开这个服务器结束，被称为一个会话。
* 当一个客户访问一个服务器时，可能会在服务器的几个页面之间切换，服务器应当通过某种方法知道这是一个客户，就需要`session`对象
* `session`对象是`HttpSession`类的实例。

常用方法：

* `long getCreationTime()`: 返回SESSION创建时间
* `public String getId()`: 返回SESSION创建时JSP引擎为它设的唯一ID号
* `public Object setAttribute(String name, Object value)`: 使用指定名称将对象绑定到此会话
* `public Object getAttribute(String name)`: 返回与此对话中的指定名称绑定在一起的对象，如果没有对象绑定在该名称下，则返回null
* `String[] getValueNames()`: 返回一个包含SESSION中所有可用属性的数组
* `int getMaxInactiveInterval()`: 返回两次请求间隔多长时间此SESSION被取消（单位秒）

session的生命周期
----------

* 创建
	* 当客户端第一次访问某个 JSP 或者 Servlet 时候，服务器会为当前会话创建一个SessionId，每次客户端向服务器发送请求时，都会将此SessionId携带过去，服务端会对此SessionId进行校验。
* 活动
	* 某次会话当中通过超连接打开的新页面属于同一次会话
	* 只要当前会话页面没有全部关闭，重新打开的浏览器窗口访问同一项目资源时属于同一次会话
	* 除非本次会话的所有页面都关闭后在重新访问某个 JSP 或者 Servlet 将会创建新的会话
	* 注意：原有会话还存在，只是这个旧的SessionId仍然存在于服务端，只不过再也没有客户端会携带它然后交予服务器校验
* 销毁
	* 调用了 session.invalidate() 方法
	* Session 过期（超时）
	* 服务器重新启动


Tomcat 默认 session 超时时间为30分钟  
设置 session 超时有两种方式：

* session.setMaxInactiveInterval(时间);//单位是秒
* 在web.xml配置
