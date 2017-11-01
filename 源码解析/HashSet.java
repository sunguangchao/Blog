package JDK;


import java.io.*;
import java.util.*;

/**
 * Created by 11981 on 2017/10/31.
 */
public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
    static final long serialVersionUID = -5024744406713321676L;

    private transient HashMap<E, Object> map;

    //Map中所有关键字都关联在同一个对象上
    private static final Object PRESENT = new Object();

    /**
     * 构造方法1：构造一个空的set背后的HashMap实例默认容量为16，装载因子为0.75
     */
    public HashSet(){
        map = new HashMap<>();
    }

    /**
     * 构造方法2：构造一个set，该set包含指定集合中的所有元素。
     * @param c
     */
    public HashSet(Collection<? extends E> c){
        map = new HashMap<>(Math.max((int)(c.size()/.75f) + 1, 16));
        addAll(c);
    }

    /**
     * 构造方法3：背后的HashMap实例拥有指定的初始容量和指定的装载因子
     */
    public HashSet(int initialCapacity, float loadFactor){
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    /**
     * 构造方法4：指定HashMap的初始容量
     * @param initialCapacity
     */
    public HashSet(int initialCapacity){
        map = new HashMap<>(initialCapacity);
    }

    /**
     * 构造方法5：构造一个空的链式哈希set
     * @param initialCapacity
     * @param loadFactor
     * @param dummy
     */
    HashSet(int initialCapacity, float loadFactor, boolean dummy){
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }

    /**
     * 返回一个set内所有元素的迭代器。元素返回没有特定的顺序
     * @return
     */
    @Override
    public Iterator<E> iterator(){
        return map.keySet().iterator();
    }

    @Override
    public int size(){
        return map.size();
    }

    @Override
    public boolean isEmpty(){
        return map.isEmpty();
    }

    /**
     * 如果set中包含指定元素则返回true
     * @param o
     * @return
     */
    @Override
    public boolean contains(Object o){
        return map.containsKey(o);
    }

    /**
     * 如果指定元素不存在，则添加进set，返回true;
     * 如果已经存在，不作修改，返回false;
     * @param e
     * @return
     */
    @Override
    public boolean add(E e){
        return map.put(e, PRESENT) == null;
    }

    @Override
    public boolean remove(Object o){
        return map.remove(o) == PRESENT;
    }

    @Override
    public void clear(){
        map.clear();
    }
    @Override
    @SuppressWarnings("unchecked")
    public Object clone(){
        try {
            HashSet<E> newSet = (HashSet<E>) super.clone();
            newSet.map = (HashMap<E, Object>) map.clone();
            return newSet;
        }catch (CloneNotSupportedException e){
            throw new InternalError(e);
        }

    }

    private void writeObject(ObjectOutputStream s) throws IOException{
        s.defaultWriteObject();
        s.writeInt(map.capacity());
        s.writeFloat(map.loadFactor());
        s.writeInt(map.size());
        for (E e : map.keySet()){
            s.writeObject(e);
        }

    }

    private void readObject(ObjectInputStream s)throws IOException, ClassNotFoundException{
        s.defaultReadObject();
        int capacity = s.readInt();
        if (capacity < 0){
            throw new InvalidObjectException("Illegal capacity: " + capacity);
        }

        float loadFactor = s.readFloat();
        if (loadFactor <= 0 || Float.isNaN(loadFactor)){
            throw new InvalidObjectException("Illegal load factor: " + loadFactor);
        }

        int size = s.readInt();
        if (size < 0){
            throw new InvalidObjectException("Illegal size: " + size);
        }

        capacity = (int) Math.min(size * Math.min(1 / loadFactor, 4.0f),
                HashMap.MAXIMUM_CAPACITY);
        // Create backing HashMap
        map = (((HashSet<?>)this) instanceof LinkedHashSet ?
                new LinkedHashMap<E,Object>(capacity, loadFactor) :
                new HashMap<E,Object>(capacity, loadFactor));

        for (int i=0; i < size; i++){
            E e = (E)s.readObject();
            map.put(e, PRESENT);
        }
    }
    public Spliterator<E> spliterator() {
        return new HashMap.KeySpliterator<E,Object>(map, 0, -1, 0, 0);
    }

}
