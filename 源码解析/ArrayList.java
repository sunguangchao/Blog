package test;

import java.util.*;

/**
 * Created by 11981 on 2017/10/10.
 */
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    //序列版本号
    private static final long serialVersionUID = 8683452581122892189L;
    //默认初始容量
    private static final int DEFAULT_CAPACITY = 10;
    //所有空实例共享的空数组实例
    private static final Object[] EMPTY_ELEMENTDATA = {};
    //存储ArrayList元素的数组，为空时指向EMPTY_ELEMENTDATA
    transient Object[] elementData;
    //ArrayList中实际数据的数量
    private int size;

    //初始化时，如果带容量，使用此构造函数
    public ArrayList(int initialCapacity){
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity " + initialCapacity);
        elementData = new Object[initialCapacity];
    }
    //初始化时，如果没有带容量，则使用此构造函数，默认为10
    public ArrayList(){
        super();
        this.elementData = EMPTY_ELEMENTDATA;
    }

    //创建一个包含collection的ArrayList
    public ArrayList(Collection<? extends E> c){
        elementData = c.toArray();
        size = elementData.length;
        //c.toArray might (incorrectly) not return Object[] (see 6260652)
        //返回的如果不是Object[]将调用Arrays.copyOf方法将其转为Object[]
        if (elementData.getClass() != Object[].class){
            elementData = Arrays.copyOf(elementData, size, Object[].class);
        }
    }


    /**将ArrayList实例的容量裁剪到list当前大小Size
     * 应用程序使用该操作降低ArrayList实例占用的存储
     * 因为清空等一些操作只改变参数而没有释放空间
     */

    public void trimToSize(){
        //来自AbstractList，用于记录操作的次数
        modCount++;
        int oldCapacity = elementData.length;
        if (size < oldCapacity){
            elementData = Arrays.copyOf(elementData, size);
        }
    }

    /**
     * 提升ArrayList实例的容量，确保它可以保存至少minCapacity的元素
     */
    public void ensureCapacity(int minCapacity){
        //如果数组当前为空，则添加元素时最小增长容量为DEFAULT_CAPACITY
        //否则，增长容量大于零即可
        int midExpend = (elementData != EMPTY_ELEMENTDATA) ? 0 : DEFAULT_CAPACITY;
        if (minCapacity > midExpend){
            ensureExplicitCapacity(minCapacity);
        }
    }

    private void ensureCapacityIntenal(int minCapacity){
        if (elementData == EMPTY_ELEMENTDATA){
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }

        ensureExplicitCapacity(minCapacity);
    }

    private void ensureExplicitCapacity(int minCapacity){
        modCount++;//list结构被改变的次数

        //当目标容量大于当前容量时才增长
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    //数组可分配的最大容量，有些虚拟机中会给数组保留头部，尝试分配更大的数组会导致内存溢出
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    //提升容量，确保它可以存储指定参数个元素
    private void grow(int minCapacity){
        int oldCapacity = elementData.length;
        //newCapacity = oldCapacity + oldCapacity * 2
        //使用移位运算符来代替乘法，效率更高
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - oldCapacity < 0)
            newCapacity = oldCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        //返回一个内容为原数组元素，大小为新容量的数组赋值给elementData
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity){
        if (minCapacity < 0)//overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    /**
     * 若列表中包含该元素时就返回true
     * @param o
     * @return
     */
    public boolean contains(Object o){
        return indexOf(o) >= 0;
    }

    /**
     * 通过遍历elementData数组来判断对象是否存在于list中，若存在，返回首次出现的index(0, size-1)
     * 若不存在，返回-1
     * 所以contains方法可以通过indexOf（Object）方法的返回值来判断对象是否被包含在list中
     * @param o
     * @return
     */
    public int indexOf(Object o){
        if (o == null){
            for (int i = 0; i < size; i++)
                if (elementData[i] == null)
                    return i;
        }else{
            for (int i = 0; i < size; i++)
                if (elementData[i].equals(o))
                    return i;
        }
        return -1;
    }

    /**
     * 返回的是传入对象在elementData数组中最后出现的index值，没有则是-1
     * @param o
     * @return
     */
    public int lastIndexOf(Object o){
        if (o == null){
            for (int i = size - 1; i > 0; i--)
                if (elementData[i] == null)
                    return i;
        }else{
            for (int i = size - 1; i > 0; i--)
                if (elementData[i].equals(o))
                    return i;
        }
        return -1;
    }

    /**
     * 返回ArrayList实例的浅表副本
     * @return
     */
    public Object clone(){
        try{
            //调用父类的clone方法返回一个对象的副本
            ArrayList<?> v = (ArrayList<?>) super.clone();
            //将返回对象的elementData数组的内容赋值为原对象elementData数组的内容
            v.elementData = Arrays.copyOf(elementData, size);
            //将副本的modCount设置为0
            v.modCount = 0;
            return v;

        }catch (CloneNotSupportedException e){
            throw new InternalError(e);
        }
    }
    //返回一个包含列表中所有元素的数组
    //返回的数组是一个安全的数组，即没有列表中的引用，是一个全新申请的数组
    public Object[] toArray(){
        return Arrays.copyOf(elementData, size);
    }


}
