package JDK;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 11981 on 2017/12/17.
 */
public abstract class CopyOnWriteArrayList<E> implements List<E>, RandomAccess, Cloneable, Serializable{
    private static final long serialVersionUID = 8673264195747942595L;

    /** The lock protecting all mutators */
    final transient ReentrantLock lock = new ReentrantLock();

    private transient volatile Object[] array;

    final Object[] getArray(){
        return array;
    }

    final void setArray(Object[] a){
        array = a;
    }

    /*创建一个空队列*/
    public CopyOnWriteArrayList(){
        setArray(new Object[0]);
    }

    public CopyOnWriteArrayList(Collection<? extends E> c){
        Object[] elements;
        if (c.getClass() == CopyOnWriteArrayList.class){
            elements = ((CopyOnWriteArrayList<?>)c).getArray();
        }else{
            elements = c.toArray();
            // c.toArray might (incorrectly) not return Object[] (see 6260652)
            if (elements.getClass() != Object[].class){
                elements = Arrays.copyOf(elements, elements.length, Object[].class);
            }
        }
        setArray(elements);
    }

    public CopyOnWriteArrayList(E[] toCopyIn){
        setArray(Arrays.copyOf(toCopyIn, toCopyIn.length, Object[].class));
    }

    public int size(){
        return getArray().length;
    }

    public boolean isEmpty(){
        return size() == 0;
    }

    private static boolean eq(Object o1, Object o2){
        return (o1 == null) ? o2 == null : o1.equals(o2);
    }

    private static int indexOf(Object o, Object[] elements, int index, int fence){
        if (o == null){
            for (int i = index; i < fence; i++){
                if (elements[i] == null)
                    return i;
            }
        }else{
            for (int i = index; i < fence; i++){
                if (o.equals(elements[i]))
                    return i;
            }
        }
        return -1;
    }

    private static int lastIndexOf(Object o, Object[] elements, int index) {
        if (o == null) {
            for (int i = index; i >= 0; i--)
                if (elements[i] == null)
                    return i;
        } else {
            for (int i = index; i >= 0; i--)
                if (o.equals(elements[i]))
                    return i;
        }
        return -1;
    }
    public boolean contains(Object o){
        Object[] elements = getArray();
        return indexOf(o, elements, 0, elements.length) >= 0;
    }

    public int indexOf(Object o){
        Object[] elements = getArray();
        return indexOf(o, elements, 0, elements.length);
    }

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list,
     * @param e
     * @param index
     * @return
     */
    public int indexOf(E e, int index){
        Object[] elements = getArray();
        return indexOf(e, elements, index, elements.length);
    }

    public int lastIndexOf(Object o){
        Object[] elements = getArray();
        return lastIndexOf(o ,elements, elements.length-1);
    }

    public Object clone(){
        try {
            CopyOnWriteArrayList<E> clone = (CopyOnWriteArrayList<E>) super.clone();
//            clone.resetLock();
            return clone;
        }catch (CloneNotSupportedException e){
            throw new InternalError();
        }
    }

    public Object[] toArray(){
        Object[] elements = getArray();
        return Arrays.copyOf(elements, elements.length);
    }

    public <T> T[] toArray(T a[]){
        Object[] elements = getArray();
        int len = elements.length;
        if (a.length < len){
            return (T[])Arrays.copyOf(elements, len, a.getClass());
        }else{
            System.arraycopy(elements, 0, a, 0, len);
            if (a.length > len)
                a[len] = null;
            return a;
        }
    }

    private E get(Object[] a, int index){
        return (E) a[index];
    }

    public E get(int index){
        return get(getArray(), index);
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            E oldValue = get(elements, index);

            if (oldValue != element){
                int len = elements.length;
                Object[] newElements = Arrays.copyOf(elements, len);
                newElements[index] = element;
                setArray(newElements);
            }else{
                setArray(elements);
            }
            return oldValue;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(E e){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            Object[] newElements = Arrays.copyOf(elements, len+1);
            newElements[len] = e;
            setArray(newElements);
            return true;
        }finally {
            lock.unlock();
        }
    }

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */

    public void add(int index, E element){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (index > len || index < 0){
                throw new IndexOutOfBoundsException("Index: "+index+
                        ", Size: "+len);
            }
            Object[] newElements;
            int numMoved = len - index;
            if (numMoved == 0){
                newElements = Arrays.copyOf(elements, len+1);
            }else{
                newElements = new Object[len+1];
                System.arraycopy(elements, 0, newElements, 0, index);
                System.arraycopy(elements, index, newElements, index+1, numMoved);
            }
            newElements[index] = element;
            setArray(newElements);
        }finally {
            lock.unlock();
        }
    }

    /**
     * Removes the element at the specified position in this list.
     * @param index
     * @return
     */
    public E remove(int index){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            E oldValue = get(elements, index);
            int numMoved = len - index - 1;
            if (numMoved == 0){
                setArray(Arrays.copyOf(elements, len - 1));
            }else{
                Object[] newElements = new Object[len - 1];
                System.arraycopy(elements, 0, newElements, 0, index);
                System.arraycopy(elements, index+1, newElements, index, numMoved);
            }
            return oldValue;
        }finally {
            lock.unlock();
        }
    }

    public boolean remove(Object o){
        Object[] snapshot = getArray();
        int index = indexOf(o, snapshot, 0, snapshot.length);
        return (index < 0) ? false : remove(o, snapshot, index);
    }

    /**
     * 不甚理解
     * @param o
     * @param snapshot
     * @param index
     * @return
     */
    private boolean remove(Object o, Object[] snapshot, int index){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] current = getArray();
            int len = current.length;
            if (snapshot != current) findIndex:{
                int prefix = Math.min(index, len);
                for (int i=0;i < prefix; i++){
                    if (current[i] != snapshot[i] && eq(o, current[i])){
                        index = i;
                        break findIndex;
                    }
                }
                if (index >= len){
                    return false;
                }
                if (current[index] == o){
                    break findIndex;
                }
                index = indexOf(o, current, index, len);
                if (index < 0)
                    return false;
            }
            Object[] newELements = new Object[len-1];
            System.arraycopy(current, 0, newELements, 0, index);
            System.arraycopy(current, index+1, newELements, index, len-index-1);
            setArray(newELements);
            return true;
        }finally {
            lock.unlock();
        }
    }

    /**
     * remove from the list all of the elements whose index is between formindex and toindex
     * @param fromIndex
     * @param toIndex
     */
    void removeRange(int fromIndex, int toIndex){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (fromIndex < 0 || fromIndex > toIndex || toIndex > len){
                throw new IndexOutOfBoundsException();
            }
            int newlen = len - (toIndex - fromIndex);
            int numMoved = len - toIndex;

            if (numMoved == 0){
                setArray(Arrays.copyOf(elements, newlen));
            }else{
                Object[] newElements = new Object[newlen];
                System.arraycopy(elements, 0, newElements, 0, fromIndex);
                System.arraycopy(elements, toIndex, newElements, fromIndex, numMoved);
                setArray(newElements);
            }
        }finally {
            lock.unlock();
        }
    }

    public boolean addIfAbsent(E e){
        Object[] snapshot = getArray();
        return indexOf(e, snapshot, 0, snapshot.length) >= 0 ?
                false : addIfAbsent(e, snapshot);
    }

    private boolean addIfAbsent(E e, Object[] snapshot){
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] current = getArray();
            int len = current.length;
            if (snapshot != current){
                int common = Math.min(snapshot.length, len);
                for (int i = 0; i < common; i++){
                    if (current[i] != snapshot[i] && eq(e, current[i]))
                        return false;
                }
                if (indexOf(e, current, common, len) >= 0)
                    return false;
            }
            Object[] newElements = Arrays.copyOf(current, len+1);
            newElements[len] = e;
            setArray(newElements);
            return true;
        }finally {
            lock.unlock();
        }
    }

    public boolean containsAll(Collection<?> c){
        Object[] elements = getArray();
        int len = elements.length;
        for (Object e : c){
            if (indexOf(e, elements, 0, len) < 0)
                return false;
        }
        return true;
    }

    /**
     * Removes from this list all of its elements that are contained in
     * the specified collection.
     * @param c
     * @return
     */
    public boolean removeAll(Collection<?> c){
        if (c == null)
            throw new NullPointerException();
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            Object[] elements = getArray();
            int len = elements.length;
            if (len != 0){
                int newlen = 0;
                // temp array holds those elements we know we want to keep
                Object[] temp = new Object[len];
                for (int i=0; i < len; i++){
                    Object element = elements[i];
                    if (!c.contains(element))
                        temp[newlen++] = element;
                }
                if (newlen != len){
                    setArray(Arrays.copyOf(temp, newlen));
                    return true;
                }
            }
            return false;
        }finally {
            lock.unlock();
        }

    }



}
