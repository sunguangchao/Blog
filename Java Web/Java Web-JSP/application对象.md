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
* `void forward(String relativeUrlPath)`使当前页重导到另一页面
* `void include(String relativeUrlPath)`在当前位置包含另一文件

config对象
----------
config对象是一个在Servlet初始化时，JSP引擎向它传递信息用的，此信息包括Servlet初始化时所要用到的参数（通过属性名和属性值构成）以及服务器有关的信息（通过传递一个ServletContext对象）。

常用方法：

* `ServletContext getServletContext()`返回含有服务器相关信息的ServletContext对象
* `String getInitParameter(String name)`返回初始化参数的值
* `Enumeration getInitParameterNames()`返回Servlet初始化所需所有参数的枚举