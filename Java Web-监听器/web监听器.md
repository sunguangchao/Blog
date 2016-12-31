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