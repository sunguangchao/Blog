1. 什么是ThreadLocal?
-----------
ThreadLocal类可以理解为线程本地变量。如果说定义了一个ThreadLocal，每个线程往这个ThreadLocal中读写是线程隔离的，互相之间不会影响。它提供了一种将可变数据通过每个线程拥有自己的独立副本从而实现线程封闭的机制。

2. 它的大致实现思路是怎样的？
-------------
Thread类有一个类型为ThreadLocal.ThreadLocalMap的实例变量threadlocals，也就是说每一个线程都有自己的ThreadLocalMap。ThreadLocalMap有自己独立的实现，可以简单地将key视为ThreadLocal，value为代码中要传入的值（实际上key并不是ThreadLocal本身，而是他的一个弱引用）。每个线程在往某个ThreadLocal里塞值的时候，都会往自己的ThreadLocalMap里存，读也是以某个ThreadLocal作为引用，在自己的map里找对应的key，从而实现了线程隔离。

三个理论基础
------------
1. 每个线程都有一个自己的ThreadLocal.ThreadLocalMap对象
2. 每个ThreadLocal对象都有一个循环计数器
3. ThreadLocal.get()取值，就是根据当前线程,获取自己线程的ThreadLocal.ThreadLocalMap，然后在这个Map中根据循环计数器获得一个特定value值。

a%b

相当于转化成二进制：a&(b-1)

reference:

* [ThreadLocal]([http://www.cnblogs.com/xrq730/p/4854813.html](http://www.cnblogs.com/xrq730/p/4854813.html))

* [ThreadLocal源码解析]([https://www.cnblogs.com/micrari/p/6790229.html)](https://www.cnblogs.com/micrari/p/6790229.html))