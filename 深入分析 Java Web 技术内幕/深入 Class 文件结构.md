JVM指令简介
------------
与类相关的JVM指令

指令  |参数  |解释
---   |---   |---
checkcast  | class  |检查类型转换，检验未通过将抛出 ClassCastException
getfield  |class/field desc |获取指定类的实例域，并将其值压入栈顶
getstatic |class/field desc |获取指定类的静态域，并将其值压入栈顶
instanceof|class            |检验对象是否是指定类的实例，如果是，则将1压入栈顶，如果不是，则将0压入栈顶。
new       |class            |创建一个对象，并将其引用值压入栈顶。

一些JVM指令集：P127，还没详细看，用到的时候记得查阅下

class 文件头的表示形式
-----------------
第一行是一个标识符，如果在JVM中，一个文件的开始为`magic = ca fe ba be`这个样子，说明是一个class文件。

JVM中常量类型：

* UTF8常量类型
	* UTF8 类型的常量由前两个字节来表示后面所存储的字符串的总字节数。
* Fieldref、Methodref常量类型
	* 描述Class中的属性项和方法
* Class 类型常量
	* 表示该类的名称，会指向另外一个UTF8类型的常量

