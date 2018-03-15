ArrayList补充：

第二段：适用于访问频繁的场景，频繁插入或者删除的场景请选用`LinkedList`。

2.ArrayList的数据结构
===========
2.1 类定义
----------
2.2 重要的全局变量
------

```java
/**
  * The array buffer into which the elements of the ArrayList are stored.
  * The capacity of the ArrayList is the length of this array buffer. Any
  * empty ArrayList with elementData == EMPTY_ELEMENTDATA will be expanded to
  * DEFAULT_CAPACITY when the first element is added.
  * ArrayList底层实现为动态数组
  */
private transient Object[] elementData;
/**
  * The size of the ArrayList (the number of elements it contains).
  * 数组长度 ：注意区分长度（当前数组已有的元素数量）和容量（当前数组可以拥有的元素数量）的概念
  * @serial
  */
private int size;
/**
  * The maximum size of array to allocate.Some VMs reserve some header words in an array.
  * Attempts to allocate larger arrays may result in OutOfMemoryError: 
  * Requested array size exceeds VM limit
  * 数组所能允许的最大长度；如果超出就会报`内存溢出异常` -- 可怕后果就是宕机
  */
private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
```

2.3 构造函数
------------

```java
/**
  * Constructs an empty list with the specified initial capacity.
  * 创建一个指定容量的空列表
  * @param  initialCapacity  the initial capacity of the list
  * @throws IllegalArgumentException if the specified initial capacity is negative
  */
public ArrayList(int initialCapacity) {
    super();
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
    this.elementData = new Object[initialCapacity];
}
/**
  * Constructs an empty list with an initial capacity of ten.
  * 默认容量为10
  */
public ArrayList() {
    this(10);
}
/**
  * Constructs a list containing the elements of the specified collection,
  * in the order they are returned by the collection's iterator.
  * 接受一个Collection对象直接转换为ArrayList
  * @param c the collection whose elements are to be placed into this list
  * @throws NullPointerException if the specified collection is null 万恶的空指针异常
  */
public ArrayList(Collection<? extends E> c){
    elementData = c.toArray();//获取底层动态数组
    size = elementData.length;//获取底层动态数组的长度
    // c.toArray might (incorrectly) not return Object[] (see 6260652)
    if (elementData.getClass() != Object[].class) {
        elementData = Arrays.copyOf(elementData, size, Object[].class);
    }
}
```


3. ArrayList存储
================
3.1 概述
-----------
1. ArrayList在新增元素中，会执行一系列额外操作(包括扩容、各类异常校验 - 基本都跟长度相关)
2. ArrayList使用尾插入法，新增元素插入到数组末尾；支持动态扩容
3. add()常见的异常主要包括：IndexOutOfBoundsException、OutOfMemoryError

3.2 add方法
-----------
```java
/**
  * Appends the specified element to the end of this list.
  * 使用尾插入法，新增元素插入到数组末尾
  *  由于错误检测机制使用的是抛异常，所以直接返回true
  * @param e element to be appended to this list
  * @return <tt>true</tt> (as specified by {@link Collection#add})
  */
public boolean add(E e){
    //调整容量，修改elementData数组的指向; 当数组长度加1超过原容量时，会自动扩容
    // Increments modCount!! add属于结构性修改
    ensureCapacityInternal(size+1);
    elementData[size++] = e;//尾部插入，长度+1
    return true;
}

/**
  * Inserts the specified element at the specified position in this list. 
  * Shifts the element currently at that position (if any) and any subsequent elements to
  * the right (adds one to their indices).
  * 支持插入一个新元素到指定下标
  * 该操作会造成该下标之后的元素全部后移（使用时请慎重，避免数组长度过大）
  * @param index index at which the specified element is to be inserted
  * @param element element to be inserted
  * @throws IndexOutOfBoundsException {@inheritDoc}
  */
public void add(int index, E element){
    //下标边界校验，不符合规则 抛出 `IndexOutOfBoundsException` 
    rangeCheckForAdd(index);
    //调整容量，修改elementData数组的指向; 当数组长度加1超过原容量时，会自动扩容
    ensureCapacityInternal(size+1);
    ////注意是在原数组上进行位移操作，下标为 index+1 的元素统一往后移动一位
    System.arraycopy(elementData, index, elementData, index+1, size-index);
    elementData[index] = element;//当前下标赋值
    size++;//数组长度+1
}
```

3.3 rangeCheckForAdd方法
----------
```java
/**
  * A version of rangeCheck used by add and addAll.
  * 下标边界校验 ： 下标必须为小于等于数组长度的正整数（Int）
  * 不符合规则 抛出 `IndexOutOfBoundsException` ，即 下标越界异常
  */
private void rangeCheckForAdd(int index){
    if (index > size || index < 0) {
        throw new IndexOutOfBoundsException(OutOfBoundsMsg(index));
    }
}
```

3.4 ensureCapacityInternal方法
------------
```java
/**
  * 调整容量，修改elementData数组的指向
  */
private void ensureCapacityInternal(int minCapacity){
	modCount++;//add操作属于结构性变动，modCount计数+1
	// overflow-conscious code 溢出敏感
	if (minCapacity - elementData.length > 0) {
		grow(minCapacity);
	}
}
```

3.5 grow方法
------
```java
/**
  * Increases the capacity to ensure that it can hold at least the
  * number of elements specified by the minimum capacity argument.
  * 当数组长度加1超过原容量时，会自动扩容 (size + 1)
  * @param minCapacity the desired minimum capacity
  */
private void grow(int minCapacity) {
    // overflow-conscious code 溢出敏感
    int oldCapacity = elementData.length;
    //区别于HashMap的2倍扩容，ArratList选择是的1.5倍扩容(向上取整) 
    //等同于 (int)Math.floor(oldCapacity*1.5) 或 (oldCapacity * 3)/2 + 1
    //PS:涉及到2次幂的操作推荐直接使用位运算，尤其是设置2次幂容量场景 eg : new ArrayList(x << 1)
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity; //最小容量设置
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity); //最大容量设置
    // minCapacity is usually close to size, so this is a win:
    //新建一个原数组的拷贝，并修改原数组，指向这个新建数组；原数组自动抛弃，等待GC回收
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

3.6内存泄漏VS内存溢出
--------------
* 内存溢出：程序在申请内存时，没有足够的内存空间供其使用
* 内存泄漏：程序在申请内存后，无法释放已申请的内存空间，一次内存泄漏危害可以忽略，但内存泄漏堆积后果严重，无论多少内存，迟早会被占光

4.ArrayList的读取
=====
4.1 get方法
------
```java
//返回指定下标的元素
public E get(int index){
    rangeCheck(index);//下标边界校验
    return elementData[index];//直接调用数组的下标方法
}
//数组访问方法
E elementData(int index){
    return (E) elementData[index];
}
```
4.2 rangeCheck方法
-------

```java
/**
  * Checks if the given index is in range.  If not, throws an appropriate
  * runtime exception.  This method does *not* check if the index is
  * negative: It is always used immediately prior to an array access,
  * which throws an ArrayIndexOutOfBoundsException if index is negative.
  * 
  * 当index>=数组长度时，抛出`IndexOutOfBoundsException` 下标越界
  * 当index为负数时，抛出`ArrayIndexOutOfBoundsException` 数组下标越界
  */
private void rangeCheck(int index){
    if (index > size) {
        throw new IndexOutOfBoundsException(OutOfBoundsMsg(index));
    }
}
```

5.ArrayList的移除
======
5.1 remove方法
----------
```java
/**
  * Removes the element at the specified position in this list.
  * Shifts any subsequent elements to the left (subtracts one from their indices).
  * 
  * 移除指定下标元素，同时大于该下标的所有数组元素统一左移一位
  * 
  * @param index the index of the element to be removed
  * @return the element that was removed from the list 返回原数组元素
  * @throws IndexOutOfBoundsException {@inheritDoc}
  */
  public E remove(int index){
    rangeCheck(index);//下标边界校验
    E oldValue = elementData(index);//获取当前坐标元素
    fastRemove(int index);
    return oldValue;//返回原数组元素
}

/**
  * Removes the first occurrence of the specified element from this list,if it is present.
  * If the list does not contain the element, it is unchanged.
  * More formally, removes the element with the lowest index <tt>i</tt> such that
  * <tt>(o==null?get(i)==null:o.equals(get(i)))</tt> (if such an element exists).
  * Returns <tt>true</tt> if this list contained the specified element
  * (or equivalently, if this list changed as a result of the call).
  * 直接移除某个元素：
  *     当该元素不存在，不会发生任何变化
  *     当该元素存在且成功移除时，返回true，否则false
  *     当有重复元素时，只删除第一次出现的同名元素 ：
  *        例如只移除第一次出现的null（即下标最小时出现的null）
  * @param o element to be removed from this list, if present
  * @return <tt>true</tt> if this list contained the specified element
  */
public boolean remove(Object o){
    //按元素移除时都需要按顺序遍历找到该值，当数组长度过长时，相当耗时
    //ArrayList允许null，需要额外进行null的处理（只处理第一次出现的null）
    if (o == null) {
        for (int index = 0; index < size; index++) {
            if (elementData[index] == null) {
                fastRemove(index);
                return true;
            }
        }
    }else{
        for (int index = 0; index < size; index++) {
            if (o.equals(elementData[index])) {
                fastRemove(index);
                return true;
            }
        }
    }
    return false;
}
```
5.2 fastRemove
------------
```java
//私有方法，除去下标边界校验以及不返回移除操作的结果
private void fastRemove(int index){
    //remove操作属于结构性改动，modCount计数+1
    modCount++;
    //需要左移的长度，因为index是从0开始计数，所以需要多减一
    int numMoved = size - index - 1;
    if (numMoved > 0) {
        //大于该下标的所有数组元素统一左移一位
        System.arraycopy(elementData, index+1, elementData, index, numMoved);
        // Let gc do its work 长度-1，同时加快gc
        elementData[--size] = null;
    }
}
```
5.3 clear
--------------
```java
//移除所有元素
public void clear(){
    modCount++;
    // Let gc do its work
    for (int i = 0; i < size; i++) {
        elementData[i] = null;
    size = 0;
    }
}
```

6.ArrayList的迭代
============
```java
    //迭代器
    ArrayList list = new ArrayList<>();
    list.add(0);
    list.add(1);
    list.add(2);
    Iterator iterator = list.iterator();
    while(iterator.hasNext()) {
        Integer o = (Integer) iterator.next();
        System.out.println(o);
    }
    //快速读取时推荐使用随机访问方式
    for (int i=0; i < list.size(); i++) {
        Integer o = (Integer)list.get(i); 
        System.out.println(o);
    }
    //循环遍历
    for(Object o : list) {
        System.out.println((Integer)o);
    }
```
7. ArrayList序列化方法解析
=================

7.1 writeObject
------------
```java
/**
  * Save the state of the <tt>ArrayList</tt> instance to a stream (that is, serialize it).
  * 序列化 ： 直接将size和element写入ObjectOutputStream
  * 
  *@serialData The length of the array backing the <tt>ArrayList</tt> instance is emitted (int)，
  *     followed by all of its elements (each an <tt>Object</tt>) in the proper order.
  */
private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException{
    // Write out element count, and any hidden stuff 序列化操作需要校验modCount
    int expectedModCount = modCount;
    s.defaultWriteObject();// 获取数组元素的Class信息
    // Write out array length 获取缓存数组长度
    s.writeInt(elementData.length);
    // Write out all elements in the proper order. 依次写入
    for (int i=0; i<size; i++)
        s.writeObject(elementData[i]);
    if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
    }
}
```
7.2 readObject
------------
```java
/**
  * Reconstitute the <tt>ArrayList</tt> instance from a stream (that is,deserialize it).
  * 反序列化：从ObjectInputStream获取size和element，再恢复到elementData
  */
private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException{
		 // Read in size, and any hidden stuff 获取数组元素的Class信息和数组长度
		s.defaultReadObject();
		// Read in array length and allocate array 获取数组长度并分配一个该长度动态数组（Object类型）
		int arrayLength = s.readInt();
		Object[] a = elementData = new Object[arrayLength];
		// Read in all elements in the proper order. 依次读出
		for(int i=0; i < size; i++){
			a[i] = s.readObject();
		}
}
```