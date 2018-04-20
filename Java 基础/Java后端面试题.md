内部类可以引用它的包含类（外部类）的成员吗？有没有什么限制？
============
一个内部类对象可以引用创建它的外部类对象的成员，包括私有成员。


数据类型之间的转换
============
- 如何将字符串转换为基本数据类型：
- 如何将基本数据类型转换为字符串：

- 调用基本数据类型对应的包装类中的方法parseXXX(String)或vlaueOf(String)即可返回相应基本类型；
- 一种方法是将基本数据类型与空字符串（”"）连接（+）即可获得其所对应的字符串；另一种方法是调用String 类中的valueOf()方法返回相应字符串。

如何实现字符串的反转及替换
===========
```java
	public static String reverse(String str) {
		if (str == null || str.length() <= 1) {
			return str;
		}
		return reverse(str.substring(1)) + str.charAt(0);
	}
```

try{}里有一个return语句，那么紧跟在这个try后的finally{}里的代码会不会执行，什么时候被执行，在return前还是后？
===============
会执行，在方法返回调用者前执行。
注意：在finally中改变返回值的做法是不好的，因为如果存在finally代码块，try中的return语句不会立马返回调用者，而是记录下返回值待finally代码块执行完毕之后再向调用者返回其值，然后如果在finally中修改了返回值，就会返回修改后的值。显然，在finally中返回或者修改返回值会对程序造成很大的困扰，C#中直接用编译错误的方式来阻止程序员干这种龌龊的事情，Java中也可以通过提升编译器的语法检查级别来产生警告或错误，Eclipse中可以在如图所示的地方进行设置，强烈建议将此项设置为编译错误。

列出一些常见的运行时异常
========
- ArithmeticException(算数异常)
- ClassCastException(类转换异常)
- IllegalArgumentException(非法参数异常)
- IndexOutOfBoundsException(下标越界异常)
- NullPointerException(空指针异常)
- SecurityException(安全异常)