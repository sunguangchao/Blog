Javabean简介  
Javabeans就是符合某种特定规范的Java类。使用Javabeans的好处就是解决代码重复编写，减少代码冗余，功能区分明确，提高了代码的维护性。

Javabean的设计原则

* 公有类
* 无参的公有构造方法
* 属性私有
* `getter`和`setter`方法

什么是Jsp动作  
动作元素为请求阶提供信息。动作元素遵循XML元素的语法，有一个包含元素名的开始标签，可以有属性、可选的内容、与开始标签匹配的结束标签。

在Jsp页面如何使用Javabeans
----------------------

* 像使用普通 java 类一样，创建 javabean 实例
* 在Jsp页面中通常使用jsp动作标签使用 javabean
  * `useBeans`动作
  * `setProperty`动作
  * `getProperty`动作

与存取Javabeans有关的标签
--------------------
<jsp:useBeans>  
作用：在jsp页面中实例化或者在指定范围内使用javabean:  
`<jsp:useBean id="标识符" class="java类名" scope="作用范围"/>`

<jsp.setProperty>  
作用：给已经实例化的Javabean对象的属性赋值，一共四种属性。

<jsp.getProperty>  
作用：获取指定Javabean对象的属性值  
`<jsp:getProperty name="JavaBean实例名" property="属性名"/>`

JavaBean的四个作用域范围
-------------
说明：使用useBeans的scope属性可以用来指定javabean的作用范围。

* page 仅在当前页面有效
* request 可以通过HttpRequest.getAttribute()方法取得JavaBean对象
* session 可以通过HttpSession.getAttribute()方法取得JavaBean对象
* application对象可以通过application.getAttribute()方法取得JavaBean对象

作用范围从小到大

Model1简介
------------
