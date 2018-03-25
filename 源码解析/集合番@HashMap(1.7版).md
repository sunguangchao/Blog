[TOC]
1.HashMap的定义
=============
* 基于哈希表的Map接口的非同步，无序实现
* 此实现提供所有可选的映射操作，并允许使用null值和null键
* 此实现假定哈希函数将元素适当分布在各桶之间，为读取操作提供稳定性能
* 迭代时间与实例容量（桶的数量）及其大小（键-值映射关系数）成正比

2.HashMap的数据结构
========
2.1重要全局变量
链表散列的数据结构(数组+链表【冲突解决方案-封闭寻址方法】)
```java
//The default initial capacity - MUST be a power of two.
static final int DEFAULT_INITIAL_CAPACITY = 16;
//The maximum capacity - MUST be a power of two <= 1<<30.
static final int MAXIMUM_CAPACITY = 1 << 30;
//The load factor used when none specified in constructor.
static final float DEFAULT_LOAD_FACTOR = 0.75f;
//The table, resized as necessary. Length MUST Always be a power of two.
transient Entry<K,V>[] table;
//The number of key-value mappings contained in this map.
transient int size;
//The next size value at which to resize (capacity * load factor).
int threshold;
//The load factor for the hash table.
final float loadFactor;
/**
  * The number of times this HashMap has been structurally modified
  * Structural modifications are those that change the number of mappings in
  * the HashMap or otherwise modify its internal structure (e.g.,
  * rehash).  This field is used to make iterators on Collection-views of
  * the HashMap fail-fast.  (See ConcurrentModificationException).
  */
transient int modCount;
```

2.3构造器
-----------
```java
public HashMap(int initialCapacity, float loadFactor) {
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
    if (initialCapacity > MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
    // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;
        this.loadFactor = loadFactor;
        //阈值为容量*负载因子和最大容量+1之间的最小值 以此值作为容量翻倍的依据(不能超过最大容量)
        threshold = (int)Math.min(capacity * loadFactor, MAXIMUM_CAPACITY + 1);
        //初始化一个2次幂的Entry类型数组 一个桶对应一个Entry对象
        table = new Entry[capacity];
        useAltHashing = sun.misc.VM.isBooted() && 
        (capacity >= Holder.ALTERNATIVE_HASHING_THRESHOLD);
        init();
}
```

2.4Entry
---------
```java
/**
  * 静态类 默认实现内部Entry接口 (接口中可定义内部接口-Map.Entry接口为Map的内部接口)
  * PS:JDK8中引入default，作用为在接口中定义默认方法实现
  */
static class Entry<K,V> implements Map.Entry<K,V> {
    final K key;//key具有引用不可变特性
    V value;
    Entry<K,V> next;//next指向下一个：单向链表，头插入
    final int hash;
    ……
}
```

3.HashMap的重要方法
=============
3.1put
---------
```java
/**
  * 存放键值对
  * 允许存放null的key和null的value
  * @return key不存在返回null，否则返回旧值
  */
public V put(K key, V vlaue){
    /**
     * 1.针对key为null的处理有两种方式：
     *  1.1从tab[0]开始向后遍历，若存在key为null的entry，则覆盖其的value
     *  1.2遍历完成但仍不存在key为null的entry，则新增key为null的entry
     * 准则：一个HashMap只允许有一个key为null的键值对
     * 注意：由于hash(null) = 0，因此key=null的键值对永远在tab[0]
     */
    if (key == null) {
        reutrn putForNullKey(value);
    }
    /**
     * 根据key的hashCode并结合hash()算法计算出新的hash值
     * 目的是为了增强hash混淆 -> 因为key.hashCode()很可能不太靠谱
     * 而HashMap是极度依赖hash来实现元素离散的，通俗来说就是
     * 尽可能的让键值对可以分到不同的桶中以便快速索引
     */
    int hash = hash(key);
    //2.根据hash计算该元素所在桶的位置 -> 即数组下标索引
    int i = indexFor(hash, table.length);
    /**
     * 3.遍历链表，若该key存在则更新value，否则新增entry
     *   判断该key存在的依据有两个：
     *     1.key的hash一致
     *     2.key一致(引用一致或字符串一致)
     *   put操作遵循：有则更新，无则新增
     */
    for (Entry<K, V> e = table[i]; e != null; e = e.next) {
        Object k;
        if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
            //值更新
            V oldValue = e.value;
            e.value = vlaue;
            //钩子方法
            e.recordAccess(this);
            //返回旧值
            return oldValue;
            
        }
        
    }
    //put操作属于结构变更操作
    modCount++;
    //无则新增
    addEntry(hash, key, vlaue, i);
    //该key不存在就返回null -> 即新增返回null
    return null;
}
```

3.2hash方法
-------------
```java
final int hash(Object k){
    int h = 0;
    if (useAltHashing) {
        if (k instanceof String) {
            return sun.misc.Hashing.stringHash32((String) k);
        }
        h = hashSeed;
    }
    //异或就是两个数的二进制形式，按位对比，相同取0，不同取一
    //此算法加入了高位计算，防止低位不变，高位变化时，造成的 hash 冲突
    h ^= k.hashCode();
    h ^= (h >>> 20) ^ (h >>> 4);
    return h ^ (h >>> 7) ^ (h >>> 4);
}
//JDK1.8 扰动函数 -> 散列值优化函数
static final int hash(Object key){
    int h;
    //把一个数右移16位即丢弃低16位，就是任何小于2^16的数，右移16后结果都为0
    //2的16次方再右移刚好就是1 同时int最大值为32位
    //任何一个数，与0按位异或的结果都是这个数本身
    //对于非null的hash值，仅在其大于等于2^16的时候才会重新调整其值
    return (key == null) ? 0 : 
        (h = key.hashCode()) ^ (h >>> 16);
}
```
3.3indexFor
---------
```java
/**
  * Int范围(2^32)从-2147483648到2147483648，加起来大概40亿空间，内存不能直接读取
  * 用之前还要先做对数组的长度取模运算，得到的余数才能用来访问数组下标
  * @Param h 根据hash方法得到h
  * @Param length 一定是2次幂
  */
static int indexFor(int h, int length) {
    //2次幂-1 返回的结果的二进制为永远是都是1 比如 15 -> 1111 (16 -> 10000)
    //与运算 只有 1 & 1 = 1 正好相当于一个“低位掩码”
    //如果length-1中某一位为0，则不论h中对应位的数字为几，对应位结果都是0，这样就让两个h取到同一个结果，hash冲突
    //同时这个操作可以保证索引不会大于数组的大小(见开头的描述)
    return h & (length-1);
}
```

3.4addEntry
------------
```java
//该方法为包访问 package java.util(本包私有性高于子类)
void addEntry(int hash, K key, V value, int bucketIndex){
	/**
     * 扩容条件：实际容量超过阈值 && 当前坐标数组非空
     * 有个优雅的设计在于，若bucketIndex处没有Entry对象，
     * 那么新添加的entry对象指向null，从而就不会有链了
     */
    if ((size >= threshold) && (null != table[bucketIndex])) {
        //最大容量每次都扩容一倍
        resize(2 * table.length);
        //重新计算hash
        hash = (null != key) ? hash(key) : 0;
        //重算index
        bucketIndex = indexFor(hash, table.length);
    }
    //新增Entry元素到数组的指定下标位置
    createEntry(hash, key, value, bucketIndex);
}

void createEntry(int hash, K key, V value, int bucketIndex){
    //获取数组中指定 bucketIndex 下标索引处的 Entry
    Entry<K, V> e = table[bucketIndex];
    /**
     * 将新创建的Entry放入 bucketIndex 索引处，
     * 并让新的Entry(next)指向原来的Entry形成链表
     * 新加入的放入链表头部 -> 类似于栈，顶部是最新的入栈元素
     * 对于"桶"的命名，笔者认为这词儿很形象生动：
     *    1.桶具有"deep深度"的属性 -> 桶深即链表长度
     *    2.桶的顶部永远是最新"扔"进入的元素
     */
    table[bucketIndex] = new Entry<>(hash, key, value, e);
    //元素数量++
    size++;
}
```
3.5关于indexFor和hash方法的进一步解读
--------------
hashCode返回的-2147483648到2147483648的int值，加起来大概40亿的映射空间。只要哈希函数映射比较均匀松散，一般很难出现碰撞key.hashCode()
但问题是40亿长度数组，内存放不下，该散列值不能直接拿来用。用之前必须先对数组长度取模运算，得到的余数才能来访问数组下标indexFor()
长度取2的整次幂，而length-1时正好相当于一个低位掩码。与操作的结果就是散列的高位全部归零，只保留低位值，用作下标访问 

10100101 11000100 00100101 
& 00000000 00000000 00001111 
00000000 00000000 00000101 //高位全部归零，只保留末四位 
但问题是，无论散列值再松散，但只取最后几位，碰撞也很严重。更要命的是如果散列本身做的不好，分布上成等差数列，就会出现规律性重复，这时候就需要扰动函数进行打散优化.
扰动函数生效： 
扰动函数 
右位移16位(32位一半)，让高半区和低半区做异或，目的是混合原始哈希码的高位和低位，以此来加大低位的随机性。而且混合后的低位包含高位的部分特征，这样高位的信息也变相保留下来。
当长度非2次幂(最后一位永远是0)，进行与运算(只有都为1得1，否则为0)，会造成最后一位永远是0，那最后一位就无法使用，导致(1)空间的巨大浪费。同时可使用的位置比原数组长度小很多，(2)进一步增加了碰撞的几率。

4.HashMap的扩容
===========
4.1性能参数
HashMap的性能瓶颈出现在扩容阶段，而扩容时机由两个重要属性决定：
1. initialCapacity:初始容量，默认为16
2. loadFactor:负载因子，衡量一个散列表空间的使用程度，负载因子越大表示散列表的装填程度越高，反之越小。||对于使用链表法的散列表来说，查找一个元素的平均时间是 O(1+a)，因此如果负载因子越大，对空间的利用更充分，然而后果是查找效率的降低；如果负载因子太小，那么散列表的数据将过于稀疏，对空间造成严重浪费；默认的的负载因子 0.75是对空间和时间效率的一个平衡选择；当容量超出此最大容量时， resize后的HashMap 容量是原容量的两倍

4.2resize方法
------------
HashMap的扩容采用数组替换的方式：
1. 先创建一个两倍于原容量的新数组
2. 遍历旧数组对新数组赋值，期间会完成新旧索引的变更
3. 新数组替换旧数组的引用
4. 重新计算阈值
```java
/**
  * Rehashes the contents of this map into a new array with a
  * larger capacity.  This method is called automatically when the
  * number of keys in this map reaches its threshold.
  * 
  * 目的：通过增加内部数组的长度的方式，从而保证链表中只保留很少的Entry对象，从而降低put(),remove()和get()方法的执行时间
  * 注意：如果两个Entry对象的键的哈希值不一样，但它们之前在同一个桶上，那么在调整以后，并不能保证它们依然在同一个桶上
  */
void resize(int newCapacity){
	/**
     * 使用临时拷贝，保证当前数组长度的时效性(参见JAVA的`观察者`模式实现)
     * 否则多线程环境下很容易造成oldCapacity的变化，
     * 进而导致由一系列过期数据的造成的错误
     */
	Entry[] oldTable = table;
	int oldCapacity = oldTable.length;

	//达到最大容量时禁止扩容 - 边界保护
	if (oldCapacity == MAXIMUM_CAPACITY) {
		threshold = Integer.MAX_VALUE;
		return;
	}

	//1.实例化一个newCapacity容量的新数组
	Entry[] newTable = new Entry(newCapacity);
	boolean oldAltHashing = useAltHashing;
    useAltHashing |= sun.misc.VM.isBooted() &&
            (newCapacity >= Holder.ALTERNATIVE_HASHING_THRESHOLD);
    boolean rehash = oldAltHashing ^ useAltHashing;
	//2.遍历旧数组对新数组赋值
    transfer(newTable, rehash);
    //3.新数组替换旧数组
    table = newTable;
    //4.重新计算阈值
    threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY+1);
}
```

4.3transfer方法
--------------
```java
void transfer(Entry[] newTable, boolean rehash){
	int newCapacity = newTable.length;

	for (Entry<K, V> e : table) {
		while (null != e) {
			Entry<K, V> next = e.next;
			if (rehash) {
				//重新计算hash 
				//null的位置还是tab[0]不变
				e.hash = null == e.key ? 0 : hash(e.key);
			}
			//重新计算下标索引(主要是因为容量变化成2倍)
			int i = indexFor(e.hash, newCapacity);

			 //注意：多线程环境可能由于执行次序非有序造成next引用变更赋值出错导致环形链接出现，从而造成死循环
			e.next = newTable[i];//??
			newTable[i] = e;
			e = next;
		}
	}
}
```

5.Fail-Fast机制
===========
5.1错误机制
-------
当使用迭代器的过程中有其他线程修改了map，将抛出ConcurrentModificationException。
5.2 remove方法
---------------
HashMap.HashIterator的remove方法
```java
/**
  * Removes and returns the entry associated with the specified key
  * in the HashMap.  Returns null if the HashMap contains no mapping
  * for this key.
  */
final Entry<K,V> removeEntryForKey(Object key){
	//计算hash
	int hash = (key == null) ? 0 : hash(key);
	//根据hash定位数组下标index索引
	int i = indexFor(hash, table.length);
	    /**
     * 记录前一个enrty元素(默认从链表首元素开始)
     *  1.table[i] 表示链表首元素 - 即桶顶部元素
     *  2.prev会记录遍历时的当前entry的前一个entry
     */
	 Entry<K, V> prev = table[i];
	 //沿着链表从头到尾顺序遍历
	 Entry<K, V> e = prev;
	 //遍历key所在链表
	 while(e != null){
	 	Entry<K, V> next = e.next;
	 	Object k;
	 	//一旦key一致，即找到该key对应entry便移除并返回该entry
	 	if (e.hash == hash &&((k = e.key) == key || (key != null && key.equals(k)))) {
	 		//remove属于结构性更新，modCount计数+1
	 		modCount++;
			//实际元素数量-1
	 		size--;
	 		/**
             * 移除当前key对应entry，实际上就是
             * 通过变更前后链接将该entry从链表中移除
             * 1.若当前key正好位于链首，则链首指向next
             * 2.若当前key不位于链首，则该key之前的元素的next指向该key的下一个元素
             */
	 		if (prev == e) {
	 			table[i] = next;
	 		}else{
	 			prev.next = next;
	 		}
	 		e.recordRemoval(this);
	 		return e;
	 	}
	 	//记录当前key对应entry
	 	prev = e;
	 	//指向下一次循环元素
	 	e = next;
	 }
	 return e;
}
```


reference
=========
* https://www.zybuluo.com/kiraSally/note/819843