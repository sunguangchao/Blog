layout: '[layout]'
title: 面试题总结
date: 2017-06-14 21:26:05
tags: 面试
categories: 面试
------
总结的一些关于Java的面试题
<!--more-->

1.Java中的原始数据类型有哪些，它们的大小及对应的封装类？
==================

| data type | representation | 封装类       |
| --------- | -------------- | --------- |
| byte      | 8-bit          | Byte      |
| char      | 16-bit         | Character |
| short     | 16-bit         | Short     |
| int       | 32-bit         | Integer   |
| float     | 32-bit         | Float     |
| long      | 64-bit         | Long      |
| double    | 64-bit         | Double    |

boolean类型单独使用是4个字节，在数组中是一个字节。
以上的都是基本数据类型，除了基本类型(primitive)和枚举类型(enumeration)，剩下的都是引用类型(reference type)。


2.面对对象的特征有哪些方面？
=========================
* 抽象：抽象是将一类对象的共同特征总结出来构造类的过程，包括数据抽象和行为抽象。抽象只关注对象有哪些行为和属性，并不关注这些行为的细节是什么？
* 继承：继承是从已有类得到继承信息创建新类的过程。提供继承信息的类被称为父类（超类、基类）；继承让变化中的软件系统有了一定的延续性，同时继承也是封装程序中可变因素的重要手段（如果不能理解请阅读阎宏博士的《Java与模式》或《设计模式精解》中关于桥梁模式的部分）。
* 封装：通常认为封装是把数据和操作数据的方法绑定起来，对数据的方法只能通过已定义的接口。面对对象的本质就是将现实世界描绘成一系列完全自治、封闭的对象。可以说，封装就是隐藏一切可隐藏的东西，只向外界提供最简单的编程接口（可以想想普通洗衣机和全自动洗衣机的差别，明显全自动洗衣机封装更好因此操作起来更简单；我们现在使用的智能手机也是封装得足够好的，因为几个按键就搞定了所有的事情）。
* 多态性：多态性是指允许不同子类型的对象对同一消息做出不同的响应。简单的说就是用同样的对象引用调用同样的方法但是做了不同的事情。多态性分为编译时的多态性和运行时的多态性。如果将对象的方法视为对象向外界提供的服务，那么运行时的多态性可以解释为：当A系统访问B系统提供的服务时，B系统有多种提供服务的方式，但一切对A系统来说都是透明的（就像电动剃须刀是A系统，它的供电系统是B系统，B系统可以使用电池供电或者用交流电，甚至还有可能是太阳能，A系统只会通过B类对象调用供电的方法，但并不知道供电系统的底层实现是什么，究竟通过何种方式获得了动力）。方法重载（overload）实现的是编译时的多态性（也称为前绑定），而方法重写（override）实现的是运行时的多态性（也称为后绑定）。运行时的多态是面向对象最精髓的东西，要实现多态需要做两件事：1). 方法重写（子类继承父类并重写父类中已有的或抽象的方法）；2). 对象造型（用父类型引用引用子类型对象，这样同样的引用调用同样的方法就会根据子类对象的不同而表现出不同的行为）。

3.Java中实现多态的机制是什么？
================
靠的是**父类或接口定义的引用变量**可以指向**子类或者具体实现类的实例对象**，而程序的调用方法在运行期才动态绑定，就是引用变量指向的具体实例对象的方法，也就是内部里正在运行的那个对象的方法，而不是引用变量的类型中定义的方法。

如何实现多态：
* 接口实现
* 继承父类重写方法
* 同一类中进行方法重载

虚拟机是如何实现多态的：  
动态绑定技术(dynamic binding)，执行期间判断所引用对象的实际类型，根据实际类型调用对应的方法。

4.float f = 3.4;是否正确？
==========
不正确，**小数默认为double类型**，3.4为双精度，将双精度型（double）赋值给浮点型（float）属于下转型（down-casting，也称为窄化）会造成精度损失，因此需要强制类型转换float f =(float)3.4; 或者写成float f =3.4F;。


5.short s1 = 1; s1 = s1 + 1;有什么错？short s1 = 1; s1 += 1;有什么错？
=============
对于short s1 = 1; s1 = s1 + 1;由于s1 + 1运算时会自动提升表达式的类型，所以结果是int型，在赋值给short类型s1时，编译器将报需要强制转换类型的错误。

对于short s1 = 1; s1 += 1;由于 +=是java语言规定的运算符，java编译器会对它进行特殊处理，因此可以正确编译。

6.int和Integer有什么区别？
==============
```java
    public static void main(String[] args) {
        Integer a = new Integer(3);
        Integer b = 3; //将3装箱成Integer类型
        int c = 3;
        System.out.println(a == b);//false
        System.out.println(a == c);//拆箱后再比较-true
        
        Integer f1 = 100, f2 = 100, f3 = 150, f4= 150;
        System.out.println(f1 == f2);//true
        System.out.println(f3 == f4);//false
    }
```
首先需要注意的是f1、f2、f3、f4四个变量都是Integer对象引用，所以下面的==运算比较的不是值而是引用。装箱的本质是什么呢？当我们给一个Integer对象赋一个int值的时候，会调用Integer类的静态方法valueOf，如果看看valueOf的源代码就知道发生了什么。
```java
public static Integer valueOf(int i){
  if(i >= IntegerCache.low && i <= IntegerCache.high)
  	return IntegerCache.cache[i + (-IntegerCache.low)]
  return new Integer(i);
}
```
简单的说，如果整型字面量的值在-128到127之间，那么不会new新的Integer对象，而是直接引用常量池中的Integer对象，所以上面f1==f2的结果是true，而f3==f4的结果是false。


7.char型变量中能不能贮存一个中文汉字？为什么？
============
char型变量是用来存储Unicode编码的字符的，unicode编码字符集中包含了汉字，所以，char型变量当然可以存储汉字。不过，如果某个特殊的汉字没有被包含在unicode编码字符集中，那么这个char型变量中就不能存储这个特殊的汉字。补充：unicode编码占用两个字节，所以char类型的变量也是占用两个字节。

8.数组有没有length()方法？String有没有length()方法？
===============
数组没有length()方法，有length的属性。String有length()方法。

9.当一个对象被当做参数传递到一个方法后，此方法可改变这个对象的属性，并可返回变化后的结果，那么这里到底是值传递还是引用传递？
==========
答：是值传递。Java语言的方法调用只支持参数的值传递。当一个对象实例作为一个参数被传递到方法中时，参数的值就是对该对象的引用。对象的属性可以在被调用过程中被改变，但对对象引用的改变是不会影响到调用者的。

10.重载(Overload)和重写(Override)的区别。重载的方法能否根据返回类型进行区分？
=============
方法的重载和重写都是实现多态的方式，区别在于前者实现的是编译时的多态性，而后者实现的是运行时的多态性。重载发生在一个类中，同名的方法如果有不同的参数列表（参数类型不同、参数个数不同或者二者都不同）则视为重载；重写发生在子类与父类之间，重写要求子类被重写方法与父类被重写方法有相同的返回类型，比父类被重写方法更好访问，不能比父类被重写方法声明更多的异常（里氏代换原则）。重载对返回类型没有特殊的要求。


11.接口是否可以继承接口？抽象类是否可实现接口？抽象类是否可继承具体类？抽象类中是否可以有静态的main方法？
=========
都可以有。
抽象类与普通类的唯一区别：就是不能创建实例和允许有abstract方法

12.构造器Constructor是否可被override？
================
构造器Constructor不能被继承，因此不能被重写Override，但可以被重载。

13.abstract class和interface的区别？
=============
答：抽象类和接口都不能够实例化，但可以定义抽象类和接口类型的引用。一个类如果继承了某个抽象类或者实现了某个接口都需要对其中的抽象方法全部进行实现，否则该类仍然需要被声明为抽象类。接口比抽象类更加抽象，因为抽象类中可以定义构造器，可以有抽象方法和具体方法，而接口中不能定义构造器而且其中的方法全部都是抽象方法。抽象类中的成员可以是private、默认、protected、public的，而接口中的成员全都是public的。抽象类中可以定义成员变量，而接口中定义的成员变量实际上都是常量。有抽象方法的类必须被声明为抽象类，而抽象类未必要有抽象方法。

抽象类的意义：  
1. 为其他子类提供一个公共的类型
2. 封装子类中重复定义的内容
3. 定义抽象方法（为了被子类实现）

抽象类与接口的区别

| 比较     | 抽象类                                      | 接口                                 |
| ------ | ---------------------------------------- | ---------------------------------- |
| 默认方法   | 抽象类可以有默认的方法实现                            | Java8之前，接口中不存在方法的实现                |
| 实现方式   | 子类使用extends关键字来继承抽象类，子类需要提供抽象类中所声明方法的实现。 | 子类使用implements来实现接口，需要提供接口中所有声明的实现 |
| 构造器    | 抽象类中可以有构造器                               | 接口中不能                              |
| 和正常类区别 | 抽象类不能被实例化                                | 接口则是完全不同的类型                        |
| 访问修饰符  | 抽象方法可以有public,protected和default等修饰符      | 接口默认是public，不能使用其他修饰符              |
| 添加新方法  | 向抽象类中添加新方法，可以提供默认的实现，因此可以不修改子类现有的代码      | 如果往接口中添加新方法，则子类中需要实现该方法            |




什么是不可变对象：
不可变对象指对象一旦被创建，状态就不能被改变。任何修改都会创建一个新的对象。如String、Integer及其他包装类

Java中创建对象的几种方式：
1. 采用new语句创建对象
2. 采用反射，调用java.lang.Class或者java.lang.reflect.Constructor类的newInstance()方法
3. 调用对象的clone()方法
4. 运用反序列手段，调用java.io.ObjectInputStream对象的readObject方法

14.Object中的公共方法有哪些：
=================
1. equals(),hashCode(),toString()
2. clone():obj.clone().getClass() == obj.getClass()
3. getClass()
4. notify(),notifyAll(),wait()


15.谈一谈Java中`==`和`equals()`的区别  
=============
Java中的数据类型，可分为两类： 

1. 基本数据类型，也称原始数据类型。byte,short,char,int,float,double,long,boolean他们之间的比较，应该用双等号`==`,比较的是他们的值。还有枚举类型也要用`==`  

2. 复合数据类型（类）
  当他们用`==`进行比较的时候，比较的是他们在`内存`中的存放地址，所以，除非是同一个new出来的对象，他们的比较后的结果为true，否则比较后结果为false。 

Java当中所有的类都是继承于Object这个基类的，在Object中的基类中定义了一个equals的方法，这个方法的`初始行为`是比较对象的`内存地址`，但在一些类库当中这个方法被覆盖(重写)掉了，如String、Integer、Date，在这些类当中equals有其自身的实现，而不再是比较类在堆内存中的存放地址了。

总结：  
对于复合数据类型之间进行equals比较，在没有覆写equals方法的情况下，他们之间的比较还是基于他们在内存中的存放位置的地址值的，因为Object的equals方法也是用双等号（==）进行比较的，所以比较后的结果跟双等号（==）的结果相同

总的来说，==用于比较是不是一个人，equals比较的是长得是否一样。

参考：[Java中equals和==的区别](http://www.cnblogs.com/zhxhdean/archive/2011/03/25/1995431.html)



15.1 equals()和hashCode()的联系
-----------------
`hashCode()`是Object类的一个方法，返回一个哈希值。如果两个对象根据`equals()`方法比较相等，那么调用这两个对象中任意一个对象的`hashCode()`方法必须产生相同的hash值。
如果两个对象根据`equals()`方法比较不相等，那么产生的哈希值不一定相等（碰撞的情况下还是会相等的）

15.2 a.hashCode()有什么用?与a.equals(b)有什么关系  
-------------------------
`hashCode()`方法是相应对象整型的 hash 值。它常用于基于hash的集合类，如 Hashtable、HashMap、LinkedHashMap等等。它与`equals()`方法关系特别紧密。根据 Java 规范，两个使用`equals()`方法来判断相等的对象，必须具有相同的 `hashcode`。  
将对象放入到集合中时,首先判断要放入对象的`hashcode`是否已经在集合中存在,不存在则直接放入集合.如果`hashcode`相等,然后通过`equals()`方法判断要放入对象与集合中的任意对象是否相等:如果`equals()`判断不相等,直接将该元素放入集合中,否则不放入.

16.静态嵌套类(Static Nested Class)和内部类(Inner Class)的不同？
============
Static Nested Class是被声明为静态的内部类，它可以不依赖于外部类实例被实例化。而通常内部类需要在外部类实例化后才能实例化。

17.String,StringBuilder和StringBuffer区别
=========
答：Java平台提供了两种类型的字符串：String和StringBuffer/StringBuilder，它们可以储存和操作字符串。其中String是只读字符串，也就意味着String引用的字符串内容是不能被改变的。而StringBuffer/StringBuilder类表示的字符串对象可以直接进行修改。StringBuilder是Java 5中引入的，它和StringBuffer的方法完全相同，区别在于它是在单线程环境下使用的，因为它的所有方面都没有被synchronized修饰，因此它的效率也比StringBuffer要高。

--------------------------------------
进程，线程之间的区别  
进程是程序运行和资源分配的基本单位，一个程序至少有一个进程，一个进程至少有一个线程。进程在执行过程中拥有独立的内存单元，而多个线程共享内存资源，减少上下文切换次数，从而效率更高。同一个进程中的多个线程可以并发执行。

守护线程  
程序运行完毕，jvm会等待非守护线程完成后关闭，但是jvm不会等待守护线程。守护线程最典型的的例子就是GC线程。

多线程的上下文切换  
指CPU控制权由一个已经正在运行的线程切换到另外一个就绪并等待获取CPU执行权的线程的过程。 

Runnable和Callable的区别:  
Runnable接口中的run方法的返回值是void，它做的事只是去执行run方法中的代码；Callable接口中的call()方法是有返回值的，是一个泛型，和Future、FutureTask配合可以用来获取异步执行的结果。`Callable+Future/FutureTask`可以获得多线程运行的结果，在等待时间太长没获取到需要的数据的情况下取消该线程的任务。


18.什么导致线程阻塞
===============
| 方法                 | 说明                                       |
| ------------------ | ---------------------------------------- |
| sleep()            | sleep() 允许 指定以毫秒为单位的一段时间作为参数，它使得线程在指定的时间内进入阻塞状态，不能得到CPU 时间，指定的时间一过，线程重新进入可执行状态。 典型地，sleep() 被用在等待某个资源就绪的情形：测试发现条件不满足后，让线程阻塞一段时间后重新测试，直到条件满足为止 |
| suspend()和resume() | 两个方法配套使用，suspend()使得线程进入阻塞状态，并且不会自动恢复，必须其对应的resume() 被调用，才能使得线程重新进入可执行状态。典型地，suspend() 和 resume() 被用在等待另一个线程产生的结果的情形：测试发现结果还没有产生后，让线程阻塞，另一个线程产生了结果后，调用 resume() 使其恢复。 |
| yield()            | yield() 使得线程放弃当前分得的 CPU 时间，但是不使线程阻塞，即线程仍处于可执行状态，随时可能再次分得 CPU 时间。调用 yield() 的效果等价于调度程序认为该线程已执行了足够的时间从而转到另一个线程 |
| wait()和notify()    | 两个方法配套使用，wait() 使得线程进入阻塞状态，它有两种形式，一种允许 指定以毫秒为单位的一段时间作为参数，另一种没有参数，前者当对应的 notify() 被调用或者超出指定时间时线程重新进入可执行状态，后者则必须对应的 notify() 被调用. |

reference:
* [Java多线程suspend()、resume()和wait()、notify()的区别](http://hovertree.com/hvtart/bjae/329665288828761a.htm)
* [为什么wait(),notify(),notifyAll()必须在同步方法/代码块中调用？](http://baikkp.blog.51cto.com/3131132/1190175)

wait方法和notify()/notifyAll()方法在放弃对象监视器时有什么区别？  
------------
wait()方法会立刻释放对象监视器，notify()/notifyAll()方法则会等待线程剩余代码执行完毕才会放弃对象监视器。

线程的sleep()方法和yield()方法有什么区别？
------------
* sleep()方法给其他线程运行机会时不考虑线程的优先级，因此会给低优先级的线程以运行机会；yield()方法只会给相同优先级或者更高优先级的线程以运行机会。
* 线程执行sleep()方法后进入阻塞(blocked)状态，而执行yield()方法后转入就绪(ready)状态
* sleep()方法声明抛出InterruptedException，而yield()方法没有声明任何异常。
* sleep()方法比yield()方法具有更好的可移植性。

请说出与线程同步以及线程调度的相关方法
-------------
- wait()：使一个线程处于等待（阻塞）状态，并且释放所持有的对象的锁；
- sleep()：使一个正在运行的线程处于睡眠状态，是一个静态方法，调用此方法要处理InterruptedException异常；
- notify()：唤醒一个处于等待状态的线程，当然在调用此方法的时候，并不能确切的唤醒某一个等待状态的线程，而是由JVM确定唤醒哪个线程，而且与优先级无关；
- notityAll()：唤醒所有处于等待状态的线程，该方法并不是将对象的锁给所有线程，而是让它们竞争，只有获得锁的线程才能进入就绪状态


19.wait()和sleep()的区别  
===============
* sleep()方法（休眠）是线程类（Thread）的静态方法，调用此方法会让当前线程暂停执行指定的时间，将执行机会（CPU）让给其他线程，但是对象的锁依然保持，因此休眠时间结束后会自动恢复（线程回到就绪状态，请参考第66题中的线程状态转换图）。
* wait()是Object类的方法，调用对象的wait()方法导致当前线程放弃对象的锁（线程暂停执行），进入对象的等待池（wait pool），只有调用对象的notify()方法（或notifyAll()方法）时才能唤醒等待池中的线程进入等锁池（lock pool），如果线程重新获得对象的锁就可以进入就绪状态。

补充：可能不少人对什么是进程，什么是线程还比较模糊，对于为什么需要多线程编程也不是特别理解。简单的说：进程是具有一定独立功能的程序关于某个数据集合上的一次运行活动，是操作系统进行资源分配和调度的一个独立单位；线程是进程的一个实体，是CPU调度和分派的基本单位，是比进程更小的能独立运行的基本单位。线程的划分尺度小于进程，这使得多线程程序的并发性高；进程在执行时通常拥有独立的内存单元，而线程之间可以共享内存。使用多线程的编程通常能够带来更好的性能和用户体验，但是多线程的程序对于其他程序是不友好的，因为它可能占用了更多的CPU资源。当然，也不是线程越多，程序的性能就越好，因为线程之间的调度和切换也会浪费CPU时间。时下很时髦的Node.js就采用了单线程异步I/O的工作模式。

```java
public class MutiThread {
    public static void main(String[] args) {
        new Thread(new Thread1()).start();
        try{
            Thread.sleep(10);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        new Thread(new Thread2()).start();
    }

    private static class Thread1 implements Runnable{

        @Override
        public void run(){
            synchronized (MutiThread.class){
                System.out.println("enter thread1");
                System.out.println("thread1 is waiting");
                try {
                    MutiThread.class.wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("thread1 is going on...");
                System.out.println("thread1 is being over!");
            }
        }
    }
    private static class Thread2 implements Runnable{
        @Override
        public void run(){
            synchronized (MutiThread.class){
                System.out.println("enter thread2...");
                System.out.println("thread2 notify other thread can release wait status...");
                MutiThread.class.notify();
                System.out.println("thread2 is sleeping ten millisecond...");

                try {
                    Thread.sleep(10);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                System.out.println("thread2 is going on...");
                System.out.println("thread2 is being over!");
            }
        }
    }
}
```
20.当一个线程如果一个对象的一个synchronized方法后，其他线程是否可以进入此对象的其他方法？
=============
1. 其他方法前是否加了synchronized关键字，如果没加，则能。
2. 如果这个方法内部调用了wait，则可以进入其他synchronized方法
3. 如果其他各个方法都加了synchronized关键字，并且内部没有调用wait，则不能
4. 如果其他方法是static，它用的同步锁是当前类的字节码，与非静态类的方法不能同步，因为非静态的方法用的是this。

21.线程的基本概念、线程的基本状态及状态之间的关系
===========
![](https://img-blog.csdn.net/20150408002007838)
说明：其中Running表示运行状态，Runnable表示就绪状态（万事具备，只欠CPU），Blocked表示阻塞状态，阻塞状态又有多中情况，可能是因为调用wait()方法进入等待池，也可能是执行同步方法或代码块进入等锁池，或者是调用了sleep()方法或join方法等待休眠或其他线程结束，或是因为发生了I/O中断。

状态：就绪，运行，synchronized阻塞，wait和sleep挂起，结束。wait必须在synchronized内部调用。
调用线程的start方法后线程进入就绪状态，线程调度系统将就绪状态的线程转为运行状态，遇到synchronized语句时，由运行状态转为阻塞，当synchronized获得锁后，由阻塞转为运行，这种情况可以调用wait方法转为挂起状态，当线程关联的代码执行完毕后，线程变为结束状态。

22.设计四个线程，其中两个线程每次对j增加1，另外两个线程对j每次减少1。写出程序
=============
```java
public class ThreadTest {
    private int j;

    public static void main(String[] args) {
        ThreadTest test = new ThreadTest();
        Inc inc = test.new Inc();
        Dec dec = test.new Dec();
        for (int i=0; i < 2; i++){
            Thread t = new Thread(inc);
            t.start();
            t = new Thread(dec);
            t.start();
        }
    }
    private synchronized void inc(){
        j++;
        System.out.println(Thread.currentThread().getName()
                + "-inc:" + j);
    }
    private synchronized void dec(){
        j--;
        System.out.println(Thread.currentThread().getName()
                + "-dec:" + j);
    }
    class Inc implements Runnable{
        public void run(){
            for (int i=0; i < 100; i++){
                inc();
            }
        }
    }
    class Dec implements Runnable{
        public void run(){
            for (int i = 0; i < 100; i++){
                dec();
            }
        }
    }
}
```


ReentrantLock
* [官方文档](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/locks/ReentrantLock.html)
* [ReentrantLock(重入锁)以及公平性](http://ifeve.com/reentrantlock-and-fairness/)
* [Java中的ReentrantLock和synchronized两种锁定机制的对比](https://www.ibm.com/developerworks/cn/java/j-jtp10264/)

```
class X{
	private final ReentrantLock lock = new ReentrantLock();
	//...

	public void m(){
		lock.lock();//block until condition holds
		try{
			//...method body
		}finally{
			lock.unlock()
		}
	}
}
```
FutureTask是什么？  
FutureTask表示一个异步运算的任务。FutureTask里面可以传入一个Callable的具体实现类，可以对这个异步运算的任务的结果进行等待获取、判断是否已经完成、取消任务等操作。当然，由于FutureTask也是Runnable接口的实现类，所以FutureTask也可以放到线程池中。

如何正确地使用wait()?
```
synchronized(obj){
    while(condition does not hold)
        obj.wait();//Release lock,and reacquires on wake up
        ...//Perform action appropriate to conditon
}
```

什么是线程局部变量(ThreadLocal)？  
当工作于多线程的对象使用ThreadLocal维护变量时，ThreadLocal为每一个使用该变量的线程分配一个独立的变量副本。所以每一个线程都可以独立地改变自己的副本，而不影响其他线程对应的变量副本；  
ThreadLocal是一种以空间换时间的做法，在每个Thread里面维护了一个`ThreadLocal.ThreadLocalMap`把数据进行隔离，数据不共享，自然没有线程安全方面的问题。  
参考：[深入研究java.lang.ThreadLocal类](http://lavasoft.blog.51cto.com/62575/51926/)

23.什么是线程池(thread pool)?
=============
在面向对象编程中，创建和销毁对象是很费时间的，因为创建一个对象要获取内存资源或者其它更多资源。在Java中更是如此，虚拟机将试图跟踪每一个对象，以便能够在对象销毁后进行垃圾回收。所以提高服务程序效率的一个手段就是尽可能减少创建和销毁对象的次数，特别是一些很耗资源的对象创建和销毁，这就是”池化资源”技术产生的原因。线程池顾名思义就是事先创建若干个可执行的线程放入一个池（容器）中，需要的时候从池中获取线程不用自行创建，使用完毕不需要销毁线程而是放回池中，从而减少创建和销毁线程对象的开销。  

1. 降低资源消耗。通过重复利用已创建的线程降低线程创建和销毁造成的消耗
2. 提高响应速度。当任务到达时，任务可以不需要等到线程创建就能立即执行
3. 提高线程的可管理性
  
常用线程池：ExecutorService

24.生产者消费者模型的作用是什么？
===============
* 通过平衡生产者的生产能力和消费者的消费能力来提升整个系统的运行效率，这是生产者模型最重要的作用。
* 解耦，这是生产者消费者模型的附带作用，解耦意味着生产者和消费者之间联系少，联系越少越可以独自发展而不需要受到相互的制约。

参考：[聊聊并发——生产者消费者模式](http://www.infoq.com/cn/articles/producers-and-consumers-mode)

通过阻塞队列实现模型：
```java
public class Producer implements Runnable {
    private final BlockingQueue<Integer> queue;
    public Producer(BlockingQueue q){
        this.queue = q;
    }
    @Override
    public void run(){
        try {
            while (true){
                Thread.sleep(1000);
                queue.put(produce());
            }
        }catch (InterruptedException e){

        }
    }

    private int produce(){
        int n = new Random().nextInt(10000);
        System.out.println("Thread: " + Thread.currentThread().getId() + " produce: " + n);
        return n;
    }
}
```

```
public class Consumer implements Runnable {
    private final BlockingQueue<Integer> queue;
    public Consumer(BlockingQueue q){
        this.queue = q;
    }

    @Override
    public void run(){
        while (true){
            try {
                Thread.sleep(2000);
                consume(queue.take());
            }catch (InterruptedException e){

            }
        }
    }

    private void consume(Integer n){
        System.out.println("Thread: " + Thread.currentThread().getId() + " consume: " + n);
    }


    public static void main(String[] args){
        BlockingQueue<Integer> q1 = new ArrayBlockingQueue<Integer>(100);
        Producer p = new Producer(q1);
        Consumer c1 = new Consumer(q1);
        Consumer c2 = new Consumer(q1);

        new Thread(p).start();
        new Thread(c1).start();
        new Thread(c2).start();
    }
}
```
25.volatile关键字
=============
* [Java理论与实践：正确使用Volatile变量](https://www.ibm.com/developerworks/cn/java/j-jtp06197.html)
* [深入理解Java内存模型——volatile](http://www.infoq.com/cn/articles/java-memory-model-4)

volatile如何保证内存可见性  

1. 当写一个volatile变量时，JMM会把该线程对应的本地内存中的共享变量刷新到主内存。
2. 当读一个volatile变量时，JMM会把该线程对应的本地内存置为无效。线程接下来将从主内存中读取共享变量。


26.synchronized关键字
=============
* [Java中synchronized的用法](http://www.importnew.com/21866.html)
* [轻松使用线程：同步不是敌人](https://www.ibm.com/developerworks/cn/java/j-threads/)
* [synchronized关键字总结](http://uule.iteye.com/blog/1104562)

[fail-fast机制](http://blog.csdn.net/chenssy/article/details/38151189)
=================
fail-fast机制是Java集合(Collection)中的一种错误机制。当多个线程对一个集合的内容进行操作时，就可能会产生fail-fast事件，并抛出ConcurrentModificationException异常

27.happens-before
============
如果两个操作之间具有happens-before关系，那么前一个操作的结果就会对会面一个可见
* 程序顺序规则：一个线程中的每个操作，happens-before于该线程中的任意后续操作
* 监视器锁规则：对一个监视器的解锁，happens-before于随后对这个监视器的加锁
* volatile变量规则：对一个volatile域的写，happens-before于任意后续对这这个volatile域的读
* 传递性：如果A happens-before B，且 B happens-before C，那么 A happens-before C
* 线程启动规则：Thread对象的start()方法happens-before于此线程的每一个动作


28.GC是什么？为什么要有GC？
==================
答：GC是垃圾收集的意思，内存处理是编程人员容易出现问题的地方，忘记或错误的内存回收会导致程序或系统的不稳定甚至崩溃，Java提供的GC功能可以自动监测对象是否超过作用域从而达到自动回收内存的目的，Java语言没有提供释放已分配内存的显示操作方法。Java程序员不用担心内存管理，因为垃圾收集器会自动进行管理。要请求垃圾收集，可以调用下面的方法之一：System.gc() 或Runtime.getRuntime().gc() ，但JVM可以屏蔽掉显示的垃圾回收调用。

垃圾回收可以有效的防止内存泄露，有效的使用可以使用的内存。垃圾回收器通常是作为一个单独的低优先级的线程运行，不可预知的情况下对内存堆中已经死亡的或者长时间没有使用的对象进行清除和回收，程序员不能实时的调用垃圾回收器对某个对象或所有对象进行垃圾回收。在Java诞生初期，垃圾回收是Java最大的亮点之一，因为服务器端的编程需要有效的防止内存泄露问题，然而时过境迁，如今Java的垃圾回收机制已经成为被诟病的东西。移动智能终端用户通常觉得iOS的系统比Android系统有更好的用户体验，其中一个深层次的原因就在于Android系统中垃圾回收的不可预知性。

补充：垃圾回收机制有很多种，包括：分代复制垃圾回收、标记垃圾回收、增量垃圾回收等方式。标准的Java进程既有栈又有堆。栈保存了原始型局部变量，堆保存了要创建的对象。Java平台对堆内存回收和再利用的基本算法被称为标记和清除，但是Java对其进行了改进，采用“分代式垃圾收集”。这种方法会跟Java对象的生命周期将堆内存划分为不同的区域，在垃圾收集过程中，可能会将对象移动到不同区域：
- 伊甸园（Eden）：这是对象最初诞生的区域，并且对大多数对象来说，这里是它们唯一存在过的区域。
- 幸存者乐园（Survivor）：从伊甸园幸存下来的对象会被挪到这里。
- 终身颐养园（Tenured）：这是足够老的幸存对象的归宿。年轻代收集（Minor-GC）过程是不会触及这个地方的。当年轻代收集不能把对象放进终身颐养园时，就会触发一次完全收集（Major-GC），这里可能还会牵扯到压缩，以便为大对象腾出足够的空间。

与垃圾回收相关的JVM参数：
* -Xms/-Xmx:堆的初始大小/堆的最大值
* -Xmn:堆中年轻代的大小
* -XX:-DisableExplicitGC:让System.gc不产生任何作用
* -XX:+PrintGCDetails:打印GC的细节
* -XX:+PrintGCDateStamps:打印GC操作的时间戳
* -XX:NewSize/XX:MaxNewSize:设置新生代大小/设置新生代最大大小
* -XX:NewRatio:可以设置新生代和老年代的比例
* -XX:PrintTenuringDistribution:设置每次新生代GC后输出幸存者乐园中对象年龄的分布
* -XX:InitialTenuringThreshold/-XX:MaxTenuringThreshold:设置老年代阀值的初始值和最大值
* -XX:TargetSurvivorRatio:设置幸存区的目标使用率

其他参考：
* [Spring + Redis 实现数据的缓存](http://www.importnew.com/22868.html)
* [谈谈Java反射机制](http://www.importnew.com/23560.html)
* [关于Spring的69个面试问答](http://www.importnew.com/11657.html)


