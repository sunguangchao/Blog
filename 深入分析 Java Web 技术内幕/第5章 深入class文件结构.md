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



与`方法`相关的JVM指令

指令  |操作数  |解释
---  |---|----
invokeinterface  |class/method desc n  |调用接口方法
invokespecial    |class/method desc    |调用超类构造方法，实例初始化方法或私有方法
invokestatic     |class/method desc    |调用静态方法
invokevirtual    |class/method desc    |调用实例方法

与`类属性`相关的JVM指令

指令  |操作数  |解释  
----|--|--
getfield  |class/field desc  |获取指定类的实例域，并将其值压入栈顶
getstatic |class/field desc |获取指定类的实例域，并将其值压入栈顶
putfield  |class/field desc |为指定类的实例域赋值
putstatic |class/field desc |为指定类的静态域赋值

常量池
---------
JVM中定义了12中类型的常量：P141

UTF8是一种字符编码格式，他可以存储多个字节长度的字符串值

Fieldref和Methodref常量类型是为了描述Class中的属性项和方法的

Class常量表示的是类的名称，它会指向另一个UTF8类型的常量，该常量存储的是该类的具体名称

NameAndType常量类型是为了表示Fieldref和Methodref的名称和类型描述做进一步说明而存在的

类信息
------
Fields和Methods定义
-----
类属性描述
----------
Javap生成的class文件结构

* LineNumberTable-P152
* LocalVariableTable-P153
