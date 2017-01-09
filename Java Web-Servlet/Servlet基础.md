Servlet基础
---------
Servlet是JSP的前身。  
什么是Servlet?  
Servlet是在服务器上运行的小程序。一个Servlet就是一个Java类，并且可以通过“请求-响应”编程模型来访问的这个驻留在服务器内存里的Servlet程序。

手工编写第一个Servlet
----------
1. 继承`HttpServlet`
2. 重写`doGet()`或者`doPost()`方法
3. 在`web.xml`中注册Servlet

Servlet生命周期阶段包括初始化、加载、实例化、服务和销毁。