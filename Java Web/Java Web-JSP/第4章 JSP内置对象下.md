什么是session?
--------------

* session表示客户端与服务器的一次会话
* 具体到Web中的 Session 指的就是用户在浏览某个网站时，从进入网站到浏览器关闭所经过的这段时间，也就是用户浏览这个网站所花费的时间。
* session实际上是一个特定的时间概念

在服务器的`内存`中保存着不同用户的 session，和用户一一对应。

session对象
--------------

* `session`对象是一个 JSP 内置对象
* `session`对象在第一个 JSP 页面被装载时自动创建，完成会话期管理。
* 从一个客户打开浏览器并连接到服务器开始，到客户关闭浏览器离开这个服务器结束，被称为一个会话。
* 当一个客户访问一个服务器时，可能会在服务器的几个页面之间切换，服务器应当通过某种方法知道这是一个客户，就需要`session`对象，session是保存用户状态的一种机制。
* `session`对象是`HttpSession`类的实例。

session常用方法：

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
  * 某次会话当中通过`超连接`打开的新页面属于同一次会话
  * 只要当前会话页面没有全部关闭，重新打开的浏览器窗口访问同一项目资源时属于同一次会话
  * 除非本次会话的所有页面都关闭后在重新访问某个 JSP 或者 Servlet 将会创建新的会话
  * 注意：原有会话还存在，只是这个旧的SessionId仍然存在于服务端，只不过再也没有客户端会携带它然后交予服务器校验
* 销毁
  * 调用了`session.invalidate()`方法
  * Session 过期（超时）
  * 服务器重新启动


Tomcat 默认 session 超时时间为30分钟   
设置 session 超时有两种方式：
1. session.setMaxInactiveInterval(时间);//单位是秒
2. 在web.xml中配置

```html
<session-config>
<session-timeout>
  10
</session-timeout>
</session-config>//单位是分钟
```

application对象：
----------------

* application对象实现了用户数据间的共享，可存放全局变量。
* application开始于服务器的启动，终止于服务器的关闭。
* 在用户前后连接或不同用户的连接中，可以对application对象的同一属性进行操作。
* 在任何地方对application对象属性的操作，都影响到其他用户对此的访问。
* 服务器的启动和关闭决定了application对象的生命
* application对象是ServletContext类的实例

常用方法：

* `public void setAttribute(String name,Object value)`使用指定名称将对象绑定到此会话。
* `public Object getAttribute(String name)`返回与此对话中的指定名称绑定在一起的对象，如果没有对象绑定在该名称下，则返回null。
* `Enumeration getAttributeNames()`返回所有可用属性的枚举
* `String getServerInfo()`返回`JSP(SERVLET)`引擎名及版本号


page对象
-----------
page对象就是指当前JSP页面本身，有点像类中的this指针，它是java.lang.Object类的实例。  
常用的方法：

* `int hashCode()`返回此Object的hash码
* `String toString()`把此Object对象转换成String类的对象
* `void notify()`唤醒一个等待的线程
* `void notifyAll()`唤醒所有等待的线程
* `void wait(int timeout)`是一个线程处于等待直到timeout结束或被唤醒
* `void wait()`是一个线程处于等待直到被唤醒

pageContext对象
-------------

* pageContext对象提供了对JSP页面所有的对象及名字空间的访问
* pageContext对象可以访问到本页面所在的session，也可以取本页面所在的application的某一属性值
* pageContext对象相当于页面中所有功能的集大成者
* pageContext对象的本类名也叫pageContext

常用方法：

* `JspWriter getOut()`返回当前客户端响应被使用的JspWriter流(out)
* `HttpSession getSession()`返回当前页中的HttpSession对象(session)
* `Object getPage()`返回当前页中的Object对象(page)
* `ServletRequest getRequest()`返回当前页的ServletRequest对象(request)
* `ServketResponse getResponse()`返回当前页的ServletResponse对象(response)
* `void setAttribute(String name, Object attribute)`设置属性及属性值
* `Object getAttribute(String name, int scope)`在指定范围内取属性的值
* `int getAttributeScope(String name)`返回某属性的作用范围
* `void forward(String relativeUrlPath)`使当前页重导到另一页面
* `void include(String relativeUrlPath)`在当前位置包含另一文件

config对象
----------
config对象是一个在Servlet初始化时，JSP引擎向它传递信息用的，此信息包括Servlet初始化时所要用到的参数（通过属性名和属性值构成）以及服务器有关的信息（通过传递一个ServletContext对象）。

常用方法：

* `ServletContext getServletContext()`返回含有服务器相关信息的ServletContext对象
* `String getInitParameter(String name)`返回初始化参数的值
* `Enumeration getInitParameterNames()`返回Servlet初始化所需所有参数的枚举

Exception对象
--------------
exception对象是一个异常对象，当一个页面在运行过程中发生了异常，就产生这个对象。如果一个JSP页面要应用此对象，就必须把isErrorPage设为true，否则无法编译。它实际上是java.lang.Throwable对象

常用方法：

* String getMessage() 返回描述异常的信息
* String toString() 返回关于异常的简短描述信息
* void printStackTrace() 显式异常及其栈轨迹
* Throwable FillInStackTrace() 重写异常的执行栈轨迹
