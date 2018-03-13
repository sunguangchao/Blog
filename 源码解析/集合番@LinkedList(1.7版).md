1.LinkedList的定义
=================
* 继承于AbstractSequentialList的双向链表，可以被当作堆栈、队列或双端队列进行操作
* 有序，非线程安全的双向链表，默认使用尾部插入法
* 适用于频繁新增或删除场景，频繁访问场景请选用ArrayList
* 插入和删除时间复杂为O(1)，其余最差O(n)
* 由于实现Deque接口，双端队列相关方法众多，会专门来讲，这里不多加详述
* 此版本基于JDK1.7

2.LinkedList的数据结构
==========

2.1类定义
--------------
```java
public class LinkedList<E>
    extends AbstractSequentialList<E>
    implements List<E>, Deque<E>, Cloneable, java.io.Serializable
```
* 继承 AbstractSequentialList，能被当作堆栈、队列或双端队列进行操作
* 实现 List 接口，能进行队列操作
* 实现 Deque 接口，能将LinkedList当作双端队列使用
* 实现 Cloneable 接口，重写 clone() ，能克隆(浅拷贝)
* 实现 java.io.Serializable 接口，支持序列化

2.2 重要全局变量
-----------
```java
	transient int size = 0;

    /**
     * Pointer to first node.
     * Invariant: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     */
    transient Node<E> first;

    /**
     * Pointer to last node.
     * Invariant: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    transient Node<E> last;
```


node方法
---------
```java
Node<E> node(int index){
	//assert isElementIndex(index)
	if (index < (size >> 1)) {
		Node<E> x = first;//若在前办边，就从前往后找
		for (int i=0; i < index; i++) {
			x = x.next;
		}
		return x;
	}else{
		Node<E> x = last;//若在后半边，就从后往前找
		for (int i = size-1; i > index; i--) {
			x = x.prev;
		}
		return x;
	}
}
```
LinkedList的移除
==========
remove方法
-------
```java
//Retrieves and removes the head(first element) of this list
public E remove(){
	return removeFirst();
}

public E reomve(){
	checkElementIndex(index);//边界校验 index >= 0 && index < size
	return unlink(node(index));//解绑操作
}

/**
  * Removes the first occurrence of the specified element from this list,if it is present. 
  * If this list does not contain the element, it is unchanged.
  * More formally, removes the element with the lowest index {@code i}  such that
  * <tt>(o==null?get(i)==null:;o.equals(get(i)))</tt> (if such an element exists).
  * Returns {@code true} if this list contained the specified element (or equivalently, 
  * if this list changed as a result of the call).
  * 直接移除某个元素：
  *     当该元素不存在，不会发生任何变化
  *     当该元素存在且成功移除时，返回true，否则false
  *     当有重复元素时，只删除第一次出现的同名元素 ：
  *        例如只移除第一次出现的null（即下标最小时出现的null）
  * @param o element to be removed from this list, if present
  * @return {@code true} if this list contained the specified element
  */
public boolean reomve(Object o){
	if (o == null) {
		for (Node<E> x = first; x != null; x = x.next) {
			if (x.item == null) {
				unlink(x);
				return true
			}
		}
	}else{
		for (Node<E> x = first; x != null; x = x.next) {
			if (o.equals(x.item)) {
				unlink(x);
				return true;
			}
		}
	}
	return false;
}
```

unlink方法
----------
```java
E unlink(Node<E> x){
	final E element = x.item;
	final Node<E> next = x.next;
	final Node<E> prev = x.prev

	//解绑前一位节点
	if (prev == null) {//如果当前节点位于链表头部
		first = next;//后一位节点放链表头部
	}else{//非链表头部
		prev.next = next;//将前一位节点的next指向下一位节点
		x.prev = null;//当前节点的前一位节点清空 ，help gc
	}

	//解绑后一位节点
	if (next == null) {
		last = prev;
	}else{
		next.prev = prev;
		x.next = null;
	}

	x.item = null;//当前节点元素清空
	size--;//链表长度-1
	modCount++;//删除操作属于结构性变动，modCount计数+1
	return element;//返回元素本身
}
```
unlinkFirst方法
----------

```java
private E unlinkFirst(Node<E> f){
	// assert f == first && f != null;
	final E element = f.item;
	final Node<E> next = f.next;
	f.item = null;
	f.next = null;// help GC
	first = next;//下一个节点作为新的头部节点
	if (next == null) {//头部已经为null，那么尾部也是null
		last = null;
	}else{
		next.prev = null;//新头部节点的prev需要清空，非闭环
	}
	size--;
	modCount++;
	return element;

}
```

LinkedList的栈实现
=============

