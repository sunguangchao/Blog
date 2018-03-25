Java为数据结构中的映射定义了一个接口java.util.Map，此接口主要有四个常用的实现类，分别是HashMap, Hashtable, LinkedHashMap和TreeMap，类继承关系如下图所示：
![](https://tech.meituan.com/img/java-hashmap/java.util.map%E7%B1%BB%E5%9B%BE.png)

针对各个实现类的特点做出一些说明：

(1)  HashMap : 它根据键的hashCode值存储数据，大多数情况下可以直接定位到它的值，因而具有快速访问的能力，但是遍历的顺序是不确定的。HashMap最多只允许一条记录的键为null，允许多条记录的值为null。HashMap非线程安全，即任一可以时刻有多个线程同时写HashMap，可能会导致数据的不一致。如果需要满足线程安全，可以用Collections的synchronizedMap方法使HashMap具有线程安全的能力，或者使用ConcurrentHashMap。

(2) Hashtable : Hashtable是遗留类，很多映射常用的功能与HashMap类似，不同的是它继承自Dictionary，并且是线程安全的，任一时间只能有一个线程能写Hashtable，并发性不如ConcurrentHashMap，因为ConcurrentHashMap引入了分段锁。Hashtable不建议在新代码中使用，不需要线程安全的场合可以用HashMap替换，需要线程安全的场合可以用ConcurrentHashMap替换。

(3) LinkedHashMap : LinkedHashMap 是HashMap的一个子类，保存了记录的插入顺序，在用Iterator遍历LinkedHashMap 时，得到的记录肯定是先插入的，也可以在构造时带参数，`按照访问次序排序`

(4) TreeMap : TreeMap实现SortedMap接口，它能把保存的记录根据键排序，默认是按照键值的升序排序，也可以指定排序的比较器，当用Iterator遍历TreeMap时，得到的记录是排过序的。如果使用排序的映射，建议使用TreeMap。使用TreeMap时，`key必须实现Comparable接口或者在构造TreeMap传入自定义的Comparator`，否则会在运行时抛出java.lang.ClassCastException。

对于上述四种Map类型的类，要求映射中的key是不可变对象。不可变对象是该对象在创建之后哈希值不会被改变。如果对象的哈希值发生变化，Map对象可能就定位不到映射的位置了。

内部实现
===========
存储结构-字段
![](https://tech.meituan.com/img/java-hashmap/hashMap%E5%86%85%E5%AD%98%E7%BB%93%E6%9E%84%E5%9B%BE.png)

这里需要弄明白两个问题：数据底层存储具体存储的是什么？这种存储方式有什么优点呢？

(1)HashMap内部有一个非常重要的字段，就是Node[] table，即哈希桶数组，明显它是一个Node的数组。

```java
static class Node<K, V> implements Map.Entry<K,V>{
  final int hash; //用来定位数组索引位置
  final K key; //key是不可变对象，所以用final修饰
  V value;
  Node<K, V> next;//链表的下一个node
  
  Node(int hash, K key, V value, Node<K,V> next) { ... }
  public final K getKey(){ ... }
  public final V getValue() { ... }
  public final String toString() { ... }
  public final int hashCode() { ... }
  public final V setValue(V newValue) { ... }
  public final boolean equals(Object o) { ... }
}
```

(2) HashMap就是使用哈希表来存储的。哈希表为了解决冲突，可以采用开放地址法和链地址法等来解决问题，Java中HashMap采用链地址法。链地址法，简单来说，就是数组加链表的结合。每个数组元素上都有一个链表结构，当数据被Hash后，得到数组下标，把数据放在对应下标元素的链表上。

```
map.put("美团", "小美");
```

系统将调用“美团”这个key的hashCode()方法得到其hashCode()值（该方法适用于每个Java对象），再通过Hash算法的后两步运算（高位运算和取模运算）来定位该键值对的存储位置，有时两个key会定位到相同的位置，表示发生了Hash碰撞。当然Hash算法计算结果越分散，Hash碰撞的概率就越小，map的存取效率就会越高。

先来看一下HashMap源码中有关构造函数的几个字段：

```java
int threshold;  //所能容纳的键值对的极限
final float loadFactor; //负载因子
int modCount; //内部结构发生变化的次数
int size; //实际存在的键值对数量
```

首先，Node[] table的初始化长度length(默认值是16)，Load factor为负载因子(默认值是0.75)，threshold是HashMap所能容纳的最大数据量的Node(键值对)个数。threshold = length * Load factor。也就是说，在数组定义好长度之后，负载因子越大，所能容纳的键值对个数越多。

在HashMap中，哈希桶数组table的长度length大小必须为2的n次方。

存在的问题：即使负载因子和Hash算法的设计再合理，也难免会出现拉链过长的情况，一旦出现拉链过长，则会严重影响HashMap的性能。于是在JDK8版本中，对数据结构做了进一步的优化，进入了红黑树。当链表长度太长（默认超过8）时，链表就转化为了红黑树，利用红黑树快速增删改查的特点提高HashMap的性能。

`>>>:无符号右移，忽略符号位，空位都以0补齐`

reference:
* [美团-重新认识HashMap](https://tech.meituan.com/java-hashmap.html)
* http://blog.csdn.net/zxt0601/article/details/77413921