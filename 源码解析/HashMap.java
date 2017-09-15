package test;

import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * Created by 11981 on 2017/9/12.
 */
public class HashMap<K, V> extends AbstractMap<K, V>
        implements Map<K,V>, Cloneable, Serializable {

    // 默认的初始容量是16，必须是2的幂。
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    // 最大容量（必须是2的幂且小于2的30次方，传入容量过大将被这个值替换）
    static final int MAXINUM_CAPACITY = 1 << 30;
    // 默认加载因子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

   /* 当对象被序列化时（写入字节序列到目标文件）时，
    * transient阻止实例中那些用此关键字声明的变量持久化；
    * 当对象被反序列化时（从源文件读取字节序列进行重构），
    * 这样的实例变量值不会被持久化和恢复。
    * HashMap是采用拉链法实现的，每一个Entry本质上是一个单向链表
    */

    transient Entry[] table;

    // HashMap的大小，它是HashMap保存的键值对的数量
    transient int size;

    // HashMap的阈值，用于判断是否需要调整HashMap的容量（threshold = 容量*加载因子）
    int threshold;

    // 加载因子实际大小
    final float loadFactor;

    // HashMap被改变的次数
    transient volatile int modCount;

    // 指定“容量大小”和“加载因子”的构造函数
    public HashMap(int initialCapacity, float loadFactor){
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity:" +
                    initialCapacity);
        // HashMap的最大容量只能是MAXIMUM_CAPACITY
        if (initialCapacity > MAXINUM_CAPACITY)
            initialCapacity = MAXINUM_CAPACITY;
        if (loadFactor < 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor:" +
                    loadFactor);

        // 找出“大于initialCapacity”的最小的2的幂
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;

        // 设置“加载因子”
        this.loadFactor = loadFactor;
        // 设置“HashMap阈值”，当HashMap中存储数据的数量达到threshold时，就需要将HashMap的容量加倍。
        threshold = (int)(capacity * loadFactor);
        // 创建Entry数组，用来保存数据
        table = new Entry[capacity];
        init();
    }

    public HashMap(int initialCapacity){
        this(initialCapacity,DEFAULT_LOAD_FACTOR);
    }

    // 默认构造函数。
    public HashMap(){
        //设置加载因子
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        //设置阈值
        threshold = (int)(DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        // 创建Entry数组，用来保存数据
        table = new Entry[DEFAULT_INITIAL_CAPACITY];
        init();
    }

    // 包含“子Map”的构造函数
    public HashMap(Map<? extends K, ? extends V> m){
        this(Math.max((int)(m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY),DEFAULT_LOAD_FACTOR);
        // 将m中的全部元素逐个添加到HashMap中
        putAllForCreate(m);
    }

    static int hash(int h){
        h ^= (h >>> 20) ^(h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    //返回索引值
    // h & (length-1)保证返回值的小于length
    static int indexFor(int h, int length){
        return h & (length - 1);
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    // 获取key对应的value
    public V get(Object key){
        if (key == null)
            return getForNullKey();
        int hash = hash(key.hashCode());
        for (Entry<K,V> e = table[indexFor(hash,table.length)]; e != null; e = e.next){
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
                return e.value;
        }
        return null;
    }

    // 获取“key为null”的元素的值
    // HashMap将“key为null”的元素存储在table[0]位置！
    private V getForNullKey(){
        for (Entry<K,V> e = table[0]; e != null; e = e.next){
            if (e.key == null)
                return e.value;
        }
        return null;
    }

    // HashMap是否包含key
    public boolean containsKey(Object key){
        return getEntry(key) != null;
    }

    // 返回“键为key”的键值对
    final Entry<K, V> getEntry(Object key){
        int hash = (key == null) ? 0 : hash(key.hashCode());
        for (Entry<K,V> e = table[indexFor(hash, table.length)]; e != null; e = e.next){
            Object k;
            if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                return e;
        }
        return null;
    }

    // 将“key-value”添加到HashMap中
    public V put(K key, V value){
        // 若“key为null”，则将该键值对添加到table[0]中。
        if (key == null)
            return putForNullKey(value);
        // 若“key不为null”，则计算该key的哈希值，然后将其添加到该哈希值对应的链表中。
        int hash = hash(key.hashCode());
        int i = indexFor(hash, table.length);
        for (Entry<K,V> e= table[i]; e != null; e = e.next){
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))){
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        // 若“该key”对应的键值对不存在，则将“key-value”添加到table中
        modCount++;
        addEntry(hash, key, value, i);
        return null;
    }

    private V putForNullKey(V value){
        for (Entry<K,V> e = table[0]; e != null; e = e.next){
            if (e.key == null){
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }
        modCount++;
        addEntry(0,null,value,0);
        return null;
    }

    // 创建HashMap对应的“添加方法”，
    // 它和put()不同。putForCreate()是内部方法，它被构造函数等调用，用来创建HashMap
    // 而put()是对外提供的往HashMap中添加元素的方法。
    private void putForCreate(K key, V value){
        int hash = (key == null) ? 0 : hash(key.hashCode());
        int i = indexFor(hash, table.length);

        // 若该HashMap表中存在“键值等于key”的元素，则替换该元素的value值
        for (Entry<K, V> e = table[i]; e != null; e = e.next){
            Object k;
            if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))){
                e.value = value;
                return;
            }
        }

        // 若该HashMap表中不存在“键值等于key”的元素，则将该key-value添加到HashMap
        createEntry(hash, key, value, i);
    }

    // 将“m”中的全部元素都添加到HashMap中。
    // 该方法被内部的构造HashMap的方法所调用。
    private void putAllForCreate(Map<? extends K, ? extends V> m){

        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator();i.hasNext();){
            Map.Entry<? extends K, ?extends V> e = i.next();
            putForCreate(e.getKey(), e.getValue());
        }
    }


    void resize(int newCapacity){
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXINUM_CAPACITY){
            threshold = Integer.MAX_VALUE;
            return;
        }

        // 新建一个HashMap，将“旧HashMap”的全部元素添加到“新HashMap”中，
        // 然后，将“新HashMap”赋值给“旧HashMap”。
        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int)(loadFactor * newCapacity);
    }

    // 将HashMap中的全部元素都添加到newTable中
    void transfer(Entry[] newTable){
        Entry[] src = table;
        int newCapacity = newTable.length;
        for (int j=0; j < src.length; j++){
            Entry<K, V> e = src[j];
            if (e != null){
                src[j] = null;
                do {
                    Entry<K, V> next = e.next;
                    int i = indexFor(e.hash, newCapacity);
                    //?
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                }while (e != null);
            }
        }
    }


    // 将"m"的全部元素都添加到HashMap中
    public void putAll(Map<? extends K, ? extends V> m){
        // 有效性判断
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0)
            return;

        // 计算容量是否足够，
        // 若“当前实际容量 < 需要的容量”，则将容量x2。
        if (numKeysToBeAdded > threshold){
            int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXINUM_CAPACITY)
                targetCapacity = MAXINUM_CAPACITY;
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            if (newCapacity > table.length)
                resize(newCapacity);
        }

        // 通过迭代器，将“m”中的元素逐个添加到HashMap中
        for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext();){
            Map.Entry<? extends K, ? extends V> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }

    // 删除“键为key”元素
    public V remove(Object key){
        Entry<K, V> e = removeEntryForKey(key);
        return (e == null ? null : e.value);
    }

    // 删除“键为key”的元素
    final Entry<K, V> removeEntryForKey(Object key){

        // 获取哈希值。若key为null，则哈希值为0；否则调用hash()进行计算
        int hash = (key == null) ? 0 : hash(key.hashCode());
        int i = indexFor(hash, table.length);
        Entry<K, V> prev = table[i];
        Entry<K, V> e = prev;

        // 删除链表中“键为key”的元素
        // 本质是“删除单向链表中的节点”
        while (e != null){
            Entry<K, V> next = e.next;
            Object k;
            if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k)))){
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }
        return e;
    }

    //删除键值对
    final Entry<K, V> removeMapping(Object o){
        if (!(o instanceof Map.Entry))
            return null;
        Map.Entry<K,V> entry = (Map.Entry<K,V>) o;
        Object key = entry.getKey();
        int hash = (key == null) ? 0 : hash(key.hashCode());
        int i = indexFor(hash, table.length);
        Entry<K,V> prev = table[i];
        Entry<K,V> e = prev;

        while (e != null) {
            Entry<K, V> next = e.next;
            Object k;
            if (e.hash == hash && e.equals(entry)) {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }
        return e;
    }

    // 清空HashMap，将所有的元素设为null
    public void clear(){
        modCount++;
        Entry[] tab = table;
        for (int i=0; i < tab.length; i++)
            tab[i] = null;
        size = 0;
    }

    // 是否包含“值为value”的元素
    public boolean containsValue(Object value){
        // 若“value为null”，则调用containsNullValue()查找
        if (value == null)
            return containsNullValue();
        // 若“value不为null”，则查找HashMap中是否有值为value的节点。
        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++)
            for (Entry e = tab[i]; e != null; e = e.next)
                if (value.equals(e.value))
                    return true;
        return false;
    }
    //检验HashMap中是否包含null值
    private boolean containsNullValue(){
        Entry[] tab = table;
        for (int i=0; i < tab.length; i++)
            for (Entry e = tab[i]; e!= null; e = e.next)
                if (e.value == null)
                    return true;
        return false;
    }

    // 克隆一个HashMap，并返回Object对象
    public Object clone(){
        HashMap<K,V> result = null;
        try{
            result = (HashMap<K,V>)super.clone();
        }catch (CloneNotSupportedException e){

        }
        result.table = new Entry[table.length];
        result.entrySet = null;
        result.modCount = 0;
        result.size = 0;
        result.init();
        result.putAllForCreate(this);
        return result;
    }

    // Entry是单向链表
    // 它是 “HashMap链式存储法”对应的链表。

    static class Entry<K,V> implements Map.Entry<K,V>{
        final K key;
        V value;
        Entry<K, V> next;
        final int hash;

        Entry(int h, K k, V v, Entry<K, V> n){
            value = v;
            next = n;
            key = k;
            hash = h;
        }

        public final K getKey(){
            return key;
        }

        public final V getValue(){
            return value;
        }

        public final V setValue(V newValue){
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry) obj;
            Object k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))){
                Object v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                    return true;
            }
            return false;
        }

        public final int hashCode(){
            return (key == null ? 0 : key.hashCode()) ^
                    (value == null ? 0 : value.hashCode());
        }

        public final String toString(){
            return getKey() + "=" + getValue();
        }

        // 当向HashMap中添加元素时，绘调用recordAccess()。这里不做任何处理
        void recordAccess(HashMap<K, V> m){

        }

        void recordRemoval(HashMap<K, V> m){

        }
    }

    // 新增Entry。将“key-value”插入指定位置，bucketIndex是位置索引。
    void addEntry(int hash, K key, V value, int bucketIndex){
        // 保存“bucketIndex”位置的值到“e”中
        Entry<K,V> e = table[bucketIndex];
        // 设置“bucketIndex”位置的元素为“新Entry”，
        // 设置e 为新Entry的下一个结点
        table[bucketIndex] = new Entry<K, V>(hash,key,value,e);
        // 若HashMap的实际大小 不小于 “阈值”，则调整HashMap的大小
        if (size++ >= threshold)
            resize(2 * table.length);
    }

    void createEntry(int hash, K key, V value, int bucketIndex){
        // 保存“bucketIndex”位置的值到“e”中
        Entry<K,V> e = table[bucketIndex];
        table[bucketIndex] = new Entry<K,V>(hash,key,value,e);
        size++;
    }

    private abstract class HashIterator<E> implements Iterator<E>{
        Entry<K,V> next;
        // expectedModCount用于实现fast-fail机制。
        int expectedModCount;
        int index;
        Entry<K,V> current;
        HashIterator(){
            expectedModCount = modCount;
            if (size > 0){
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
        }

        public final boolean hashNext(){
            return next != null;
        }

        final Entry<K,V> nextEntry(){
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Entry<K,V> e = next;
            if (e == null){
                throw new NoSuchElementException();
            }

            // 一个Entry就是一个单向链表
            // 若该Entry的下一个节点不为空，就将next指向下一个节点;
            // 否则，将next指向下一个链表(也是下一个Entry)的不为null的节点。
            if ((next = e.next) == null){
                Entry[] t = table;
                while (index < t.length && (next = t[index++]) == null)
                    ;
            }
            current = e;
            return e;
        }

        // 删除当前元素
        public void remove(){
            if (current == null)
                throw new IllegalArgumentException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            Object k = current.key;
            current = null;
            HashMap.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }
    }
}
