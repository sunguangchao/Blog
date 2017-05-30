Servlet基础
=================
Servlet是JSP的前身。

什么是Servlet?  
--------------
Servlet是在服务器上运行的小程序。一个Servlet就是一个Java类，并且可以通过“请求-响应”编程模型来访问的这个驻留在服务器内存里的Servlet程序。

手工编写第一个Servlet
----------
1. 继承`HttpServlet`
2. 重写`doGet()`或者`doPost()`方法
3. 在`web.xml`中注册Servlet

Servlet执行流程
![](http://o90jubpdi.bkt.clouddn.com/servlet%E6%89%A7%E8%A1%8C%E6%B5%81%E7%A8%8B.png)

Servlet生命周期
Servlet生命周期阶段包括初始化、加载、实例化、服务和销毁。
![](http://o90jubpdi.bkt.clouddn.com/servlet%E7%94%9F%E5%91%BD%E5%91%A8%E6%9C%9F.png)
1. 初始化阶段，调用init()方法
2. 响应客户端请求阶段，调用service()方法。由service()方法根据提交方式选择执行doGet()或doPost()方法

在下列时刻Servlet容器装载Servlet:
1. Sevlet容器启动时自动装载某些Servlet，实现它只需要在`web.xml`文件的`<Servlet></Servlet>`之间添加如下代码：`<loadon-startup>1</loadon-startup>`，数字越小代表优先级越高。
2. 在Servlet容器启动后，客户首次向Servlet发送请求。
3. Servlet类被更新后，重新装载Servlet



Servlet与九大内置对象

| JSP对象       | 怎样获得                  |
| ----------- | --------------------- |
| out         | resp.getWriter        |
| request     | service方法中的req参数      |
| response    | service方法中的resp参数     |
| session     | req.getSession()参数    |
| application | getServletContext()参数 |
| exception   | Throwable             |
| page        | this                  |
| pageContext | PageContext           |
| Config      | getServletConfig参数    |

