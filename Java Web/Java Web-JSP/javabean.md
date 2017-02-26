Javabean简介  
Javabeans就是符合某种特定规范的Java类。使用Javabeans的好处就是解决代码重复编写，减少代码冗余，功能区分明确，提高了代码的维护性。

Javabean的设计原则

* 公有类
* 无参的公有构造方法
* 属性私有
* `getter`和`setter`方法

什么是Jsp动作
动作元素为请求阶提供信息

在Jsp页面如何使用Javabeans

* 像使用普通 java 类一样，创建 javabean 实例
* 在 Jsp 页面中通常使用 jsp 动作标签使用 javabean
	* `useBeans`动作
	* `setProperty`动作
	* `getProperty`动作

**<jsp:useBeans>**

作用：在jsp页面中实例化或者在指定范围内使用javabean:  
`<jsp:useBean id="标识符" class="java类名" scope="作用范围"/>`

**<jsp.setProperty>**

作用：给已经实例化的 Javabean 对象的属性赋值，一种四种属性。

**<jsp.getProperty>**

作用：获取指定Javabean对象的属性值  
`<jsp:getProperty name="JavaBean实例名" property="属性名"/>`
