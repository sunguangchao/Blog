package JDK;

import java.io.Serializable;
import java.util.*;

/**
 * Created by 11981 on 2017/10/22.
 */
public class Hashtable<K,V> extends Dictionary<K,V> implements Map<K,V>, Cloneable, Serializable{
    /**
     * 哈希表中存放数据的地方
     */
    private transient Entry<?,?>[] table;
    /**
     * 哈希表中所有Entry的数目
     */

    private transient int count;
    /**
     * 重新哈希的阈值（threshold = (int)capacity * loadFactor）
     */

    private int threshold;
    //装载因子

    private float loadFactor;
    /**
     * Hashtable的结构修改次数。结构修改指的是改变Entry数目或是修改内部结构（如rehash）
     * 这个字段用来使Hashtable的集合视图fail-fast的。
     */

    private int modCount = 0;

    private static final long serialVersionUID = 1421746759512286392L;

    /**
     * 构造函数一：构造一个指定容量和装载因子的空哈希表
     * @param initialCapacity
     * @param loadFactor
     */
    public Hashtable(int initialCapacity, float loadFactor){
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal capacity:" + initialCapacity);
        if (loadFactor < 0 || Float.isNaN(loadFactor)){
            throw new IllegalArgumentException("Illegal load:" + loadFactor);
        }
        if (initialCapacity == 0)
            initialCapacity = 1;
        this.loadFactor = loadFactor;
        table = new Entry<?,?>[initialCapacity];
        threshold = (int)Math.min(initialCapacity*loadFactor, MAX_ARRAY_SIZE);
    }

    /**
     * 构造函数二：构造一个指定容量的空哈希表
     * @param initialCapacity
     */

    public Hashtable(int initialCapacity){
        this(initialCapacity, 0.75f);
    }

    /**
     * 构造函数三：默认构造函数，容量为11，装载因子为0.75
     */

    public Hashtable(){
        this(11 , 0.75f);
    }

    /**
     * 构造函数四：构造一个包含子Map的构造函数，
     * 容量为足够容纳指定Map中元素的2的次幂，默认装载因子0.75
     */
    public Hashtable(Map<? extends K, ? extends V> map){
        this(Math.max(2*map.size(), 11), 0.75f);
        putAll(map);
    }
    public synchronized int size(){
        return count;
    }

    public synchronized boolean isEmpty(){
        return count == 0;
    }

    /**
     * 返回Hashtable中所有关键字的枚举集合，方法由synchronize修饰，支持同步调用
     * @return
     */
    public synchronized Enumeration<K> keys(){
        return this.<K>getEnumeration(KEYS);
    }

    public synchronized Enumeration<V> elements(){
        return this.<V>getEnumeration(VALUES);
    }

    /**
     * 测试Hashtable中是否有关键字映射到指定值上。contains(value)比containsKey(key)方法耗时多一些。
     * @param value
     * @return
     */
    public synchronized boolean contains(Object value){
        //Hashtable中的键值对的value不能为null，否则抛出异常NullPointerException
        if (value == null)
            throw new NullPointerException();
        Entry<?,?> tab[] = table;
        //从后往前遍历table数组中的元素(Entry)
        for (int i=tab.length; i-- > 0; ){
            //对于每个Entry（单向链表），逐个遍历，判断结点的值是否等于value
            for (Entry<?,?> e = tab[i]; e != null; e = e.next){
                if (e.value.equals(value)){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsValue(Object value){
        return contains(value);
    }

    /**
     * 测试指定key是否存在
     * @param key
     * @return
     */
    public synchronized boolean containsKey(Object key){
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        //关键字key映射的bucket下标
        int index = (hash & 0x7FFFFFFF) % tab.length;
        //遍历单链表找到和key相等的元素
        for (Entry<?,?> e = tab[index]; e != null; e = e.next){
            if ((e.hash == hash) && e.key.equals(key)){
                return true;
            }
        }
        return false;
    }

    /**
     * 返回指定关键字key的value值，不存在则返回null
     * @param key
     * @return
     */
    public synchronized V get(Object key){
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        //计算指定关键字映射的哈希槽
        int index = (hash&0x7FFFFFFF) % tab.length;
        //遍历单向链表
        for (Entry<?,?> e = tab[index]; e != null; e = e.next){
            if ((e.hash == hash) && e.key.equals(key)){
                return (V)e.value;
            }
        }
        return null;
    }
    /**
     * 分配数组最大容量，一些虚拟机会保存数组的头字，试图分配更大的数组会导致OOM（OutOfMemoryError）：请求的数组容量超出VM限制
     */

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * 容量增长时需要内部重新组织Hashtable，以便有效率的访问
     * 当Hashtable中关键字数据超出容量与装载因子之积时自动调用该方法
     *
     */
    protected void rehash(){
        int oldCapacity = table.length;//旧的容量
        Entry<?,?>[] oldMap = table;//旧Entry数组

        //新容量等于旧容量乘以2加1
        int newCapacity = (oldCapacity << 1) + 1;
        //溢出检测
        if (newCapacity - MAX_ARRAY_SIZE > 0){
            if (oldCapacity == MAX_ARRAY_SIZE)
                return;
            newCapacity = MAX_ARRAY_SIZE;
        }
        //申请新的Entry数组
        Entry<?,?>[] newMap = new Entry<?,?>[newCapacity];
        //修改modCount
        modCount++;
        //修改新阈值
        threshold = (int)Math.min(newCapacity*loadFactor, MAX_ARRAY_SIZE+1);
        //把新的数组指向table
        table = newMap;
        //从后向前遍历旧表每一个槽中的链表的每一个Entry元素，将其重新哈希到新表中
        for (int i=oldCapacity; i-->0; ){
            for (Entry<K,V> old = (Entry<K, V>) oldMap[i]; old != null; ){
                Entry<K,V> e = old;
                old = old.next;

                int index = (e.hash & 0x7FFFFFFF) % newCapacity;
                //将e插入index槽中当前链表的开头
                e.next = (Entry<K, V>) newMap[index];
                newMap[index] = e;
            }
        }
    }

    /**
     * 添加新的Entry元素
     * @param hash
     * @param key
     * @param value
     * @param index
     */
    private void addEntry(int hash, K key, V value, int index){
        modCount++;
        Entry<?,?> tab[] = table;
        //超过阈值，需要重新哈希
        if (count > threshold){
            rehash();
            tab = table;
            hash = key.hashCode();
            index = (hash & 0x7FFFFFFF)%tab.length;
        }

        //创建新的entry，并插入index槽的中链表的头部
        Entry<K,V> e = (Entry<K, V>) tab[index];
        tab[index] = new Entry<>(hash, key, value, e);
        count++;
    }

    private <T> Enumeration<T> getEnumeration(int type){
        if (count == 0){
            return Collections.emptyEnumeration();
        }else{
            return new Enumerator<>(type, true);
        }
    }

    private <T>Iterator<T> getIterator(int type){
        if (count == 0){
            return Collections.emptyIterator();
        }else{
            return new Enumerator<>(type, true);
        }
    }
    //将指定的key映射到指定的value。key和value都不能为null
    public synchronized V put(K key, V value){
        if (value == null)
            throw new NullPointerException();
        //确保key在Hashtable中不存在，若存在则更新value，返回旧值
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        Entry<K,V> entry = (Entry<K, V>) tab[index];
        for (; entry != null; entry = entry.next){
            if ((entry.hash == hash) && entry.key.equals(key)){
                V old = entry.value;
                entry.value = value;
                return old;
            }
        }
        //不存在，则添加元素
        addEntry(hash, key, value, index);
        return null;
    }

    /**
     * 删除关键字
     * @param key
     * @return
     */

    public synchronized V remove(Object key){
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash&0x7FFFFFFF)%tab.length;
        Entry<K, V> e = (Entry<K, V>) tab[index];
        for (Entry<K, V> prev = null; e != null; prev = e, e = e.next){
            //删除e
            if ((e.hash == hash) && e.key.equals(key)){
                modCount++;
                if (prev != null)
                    prev.next = e.next;
                else
                    tab[index] = e.next;

                count--;
                V oldValue = e.value;
                e.value = null;
                return oldValue;
            }
        }
        return null;
    }
    /**
     * 将指定Map中的所有映射都拷贝到Hashtable中，已经存在的Key对应的value值会被更新
     * @param t mappings to be stored in this map
     * @throws NullPointerException if the specified map is null
     */
    public synchronized void putAll(Map<? extends K, ? extends V> t){
        for (Map.Entry<? extends K, ? extends V> e : t.entrySet())
            put(e.getKey(), e.getValue());
    }

    public synchronized void clear(){
        Entry<?, ?> tab[] = table;
        modCount++;
        for (int index = tab.length; --index >= 0; )
            tab[index] = null;
        count = 0;
    }

    public synchronized Object clone(){
        try {
            Hashtable<?,?> t = (Hashtable<?, ?>) super.clone();
            t.table = new Entry<?, ?>[table.length];
            for (int i=table.length; i-- > 0; ){
                t.table[i] = (table[i] != null) ? (Entry<?, ?>) table[i].clone() : null;
            }
            t.keySet = null;
            t.entrySet = null;
            t.values = null;
            t.modCount = 0;
            return t;
        }catch (CloneNotSupportedException e){
            throw new InternalError(e);
        }
    }

    public synchronized String toString(){
        int max = size() - 1;
        if (max == -1)
            return "{}";
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<K,V>> it = entrySet().iterator();

        sb.append("{");
        for (int i=0; ; i++){
            Map.Entry<K,V> e = it.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key == this ? "(this map)" : key.toString());
            sb.append('=');
            sb.append(value == this ? "(this map)" : value.toString());
            if (i == max)
                return sb.append("}").toString();
            sb.append(", ");
        }
    }
    private transient volatile Set<K> keySet = null;
    private transient volatile Set<Map.Entry<K,V>> entrySet;
    private transient volatile Collection<V> values = null;

    public Set<K> keySet(){
        if (keySet == null)
            keySet = Collections.synchronizedSet(new KeySet());
        return keySet();
    }

    private class KeySet extends AbstractSet<K>{
        public Iterator<K> iterator(){
            return getIterator(KEYS);
        }
        public int size(){
            return count;
        }
        public boolean contains(Object o){
            return containsKey(o);
        }
        public boolean remove(Object o){
            return Hashtable.this.remove(o) != null;
        }
        public void clear(){
            Hashtable.this.clear();
        }
    }
    public Set<Map.Entry<K, V>> entrySet(){
        if (entrySet() == null)
            entrySet = Collections.synchronizedSet(new EntrySet());
        return entrySet;
    }

    private class EntrySet extends AbstractSet<Map.Entry<K,V>>{
        public Iterator<Map.Entry<K,V>> iterator(){
            return getIterator(ENTRIES);
        }

        public boolean add(Map.Entry<K,V> o){
            return super.add(o);
        }

        public boolean contains(Object o){
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>)o;
            Object key = entry.getKey();
            Entry<?,?>[] tab = table;
            int hash = key.hashCode();
            int index = (hash & 0x7FFFFFFF) % tab.length;

            for (Entry<?,?> e = tab[index]; e != null; e = e.next)
                if (e.hash == hash && e.equals(entry))
                    return true;
            return false;
        }

        public boolean remove(Object o){
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> entry = (Map.Entry<?,?>) o;
            Object key = entry.getKey();
            Entry<?,?>[] tab = table;
            int hash = key.hashCode();
            int index = (hash & 0x7FFFFFFF) % tab.length;

            Entry<K,V> e = (Entry<K,V>) tab[index];

            for (Entry<K,V> prev = null; e != null; prev = e, e = e.next){
                if (e.hash == hash && e.equals(entry)){
                    modCount++;
                    if (prev != null)
                        prev.next = e.next;
                    else
                        tab[index] = e.next;
                    count--;
                    e.value = null;
                    return true;
                }
            }
            return false;

        }
        public int size(){
            return count;
        }

        public void clear(){
            Hashtable.this.clear();
        }

    }

    public Collection<V> values(){
        if (values() == null)
            values = Collections.synchronizedCollection(new ValueCollection());
        return values;
    }

    private class ValueCollection extends AbstractCollection<V>{
        public Iterator<V> iterator(){
            return getIterator(VALUES);

        }

        public int size(){
            return count;
        }

        public boolean contains(Object o){
            return containsValue(o);
        }

        public void clear(){
            Hashtable.this.clear();
        }
    }

    private static class Entry<K,V> implements Map.Entry<K,V>{
        final int hash;
        final K key;
        V value;
        Entry<K,V> next;

        protected Entry(int hash, K key, V value, Entry<K,V> next){
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        protected Object clone(){
            return new Entry<>(hash, key, value, (next == null ? null : (Entry<K,V>)next.clone()));
        }

        public K getKey(){
            return key;
        }

        public V getValue(){
            return value;
        }

        public V setValue(V value){
            //Hashtable不允许空值
            if (value == null)
                throw new NullPointerException();
            V oldValue = this.value;
            this.value = value;
            //返回原先的值
            return oldValue;
        }

        public boolean equals(Object o){
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;
            //先进行为null的判断，key和value都相同返回true
            return (key == null ? e.getKey()== null : e.getKey().equals(key)) &&
                    (value == null ? e.getValue() == null : value.equals(e.getValue()));
        }

        public int hashCode(){
            //hash值只与关键字key有关，hashCode需要与值value的hashCode异或
            return hash ^ Objects.hashCode(value);
        }

        public String toString(){
            return key.toString() + "=" + value.toString();
        }
    }

    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int ENTRIES = 2;

    private class Enumerator<T> implements Enumeration<T>, Iterator<T>{
        Entry<?,?> table[] = Hashtable.this.table;//由Hashtable的table数组支持
        int index = table.length;//table数组的长度
        Entry<?,?> entry = null;//下一个返回的元素
        Entry<?,?> lastReturned = null;//上一次返回的元素
        int type;//类型：KEYS，VALUES,Entries

        //表明当前枚举是作为一个迭代器还是一个枚举类型（true表示迭代器）
        boolean iterator;

        protected int exceptedModCount = modCount;

        Enumerator(int type, boolean iterator){
            this.type = type;
            this.iterator = iterator;
        }

        public boolean hasMoreElements(){
            Entry<?,?> e = entry;
            int i = index;
            Entry<?,?>[] t = table;

            while (e == null && i > 0){
                e = t[--i];
            }
            entry = e;
            index = i;
            return e != null;
        }

        public T nextElement(){
            Entry<?, ?> et = entry;
            int i = index;
            Entry<?,?>[] t = table;
            while (et == null && i > 0){
                et = t[--i];
            }
            entry = et;
            index = i;
            if (et != null){
                Entry<?,?> e = lastReturned = entry;
                entry = e.next;
                return type == KEYS ? (T)e.key : (type == VALUES ? (T)e.value : (T)e);
            }
            throw new NoSuchElementException("Hashtable Enumeration");
        }

        public boolean hasNext(){
            return hasMoreElements();
        }

        public T next(){
            //检测并发修改异常
            if (modCount != exceptedModCount)
                throw new ConcurrentModificationException();
            return nextElement();
        }

        public void remove(){
            if (!iterator)
                throw new UnsupportedOperationException();
            if (lastReturned == null)
                throw new IllegalStateException("Hashtable Enumeration");
            if (modCount != exceptedModCount)
                throw new ConcurrentModificationException();
            synchronized (Hashtable.this){
                Entry<?,?>[] tab = Hashtable.this.table;
                int index = (lastReturned.hash & 0x7FFFFFFF) % tab.length;

                Entry<K,V> e = (Entry<K, V>) tab[index];
                for (Entry<K,V> prev = null; e != null; prev = e, e = e.next){
                    if (e == lastReturned){
                        modCount++;
                        exceptedModCount++;
                        if (prev == null){
                            tab[index] = e.next;
                        }else{
                            prev.next = e.next;
                        }
                        count--;
                        lastReturned = null;
                        return;
                    }

                }
                throw new ConcurrentModificationException();
            }
        }
    }
}
