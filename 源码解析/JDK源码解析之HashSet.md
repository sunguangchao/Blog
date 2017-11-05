HashSet

Set的特点：没有顺序，且不可重复。无顺序，由于散列的缘故；不可重复，HashMap的key就是不能重复的。HashSet就是基于HashMap的key来实现的。

定义
---------

```java
public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable
```

HashSet继承了AbstractSet抽象类，并实现了Set<E>, Cloneable, Serializable接口。

Set接口的定义：

```java
public interface Set<E> extends Collection<E>{
	//Query operation
	int size();
	boolean isEmpty();
	boolean contains(Object o);
	Iterator<E> iterator();
	<T> T[] toArray(T[] a);
	//Modification Operations
	boolean add(E e);
	boolean remove(Object o);
	//Bulk Operations
	boolean containsAll(Collection<?> c);
	boolean retainAll(Collection<?> c);
	boolean removeAll(Collection<?> c);
	void clear();
	//Comparison and hashing
	boolean equals(Object o);
	int hashCode();
}
```

底层存储
----------

```java
    private transient HashMap<E, Object> map;

    //Map中所有关键字都关联在同一个对象上
    private static final Object PRESENT = new Object();
```

HashSet用HashMap的key来保存数据，用一个静态常量PRESENT来充当HashMap的value。

增加和删除
----------

```java
 @Override
    public boolean add(E e){
        return map.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o){
        return map.remove(o) == PRESENT;
    }
    @Override
    public boolean addAll(Collection<? extends E> c){
        boolean modified = false;
        Iterator<? extends E> e = c.iterator();
        while(e.hasNext()){
            if (add(e.next())) {
                modified = true;
            }
        }
        return modified;
    }
    public boolean removeAll(Collection<?> c){
        boolean modified = false;
        // 判断当前HashSet元素个数和指定集合c的元素个数，目的是减少遍历次数
        if (size() > c.size()) {
            // 如果当前HashSet元素多，则遍历集合c，将集合c中的元素一个个删除
            for (Iterator<?> i = c.iterator(); i.hasNext(); )
                modified |= remove(i.next()); 
        }else{
            // 如果集合c元素多，则遍历当前HashSet，将集合c中包含的元素一个个删除
            for (Iterator<?> i = iterator(); i.hasNext(); ){
                if(c.contains(i.next())){
                    i.remove();
                    modified = true;
                }
            }
        }
        return modified;
    }

```

reference:

* [­给jdk写注释系列之jdk1.6容器(6)-HashSet源码解析&Map迭代器](http://www.importnew.com/17602.html)