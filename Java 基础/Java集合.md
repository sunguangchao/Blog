拆箱和装箱
----------------
创建一个java.lang.Interger对象数组
```
//creates an empty array of 5 elements
Integer[] integers = new Interger[5];

//creates an array of 5 elements with values;
Integer[] integers = new Integer[]{ Integer.value(1),
Integer.valueOf(2),
Integer.valueOf(3),
Integer.valueOf(4),
Integer.valueOf(5)
}
```

拆箱和装箱
```
int value = 238;
Integer boxedValue = Integer.valueOf(value);
int intValue = boxedValue.intValue();
```

解析和转换装箱的类型
```
String characterNumeric = "238";//String to Integer
Integer convertedValue = Integer.parseInt(characterNumeric);

Integer boxedValue = Integer.valueOf(238);//Integer to String
String characterNumeric = boxedValue.toString();
```

迭代变量(Iterable)
------
如果一个集合实现了`java.lang.Iterable`，那么该集合被称为迭代变量集合。可以从一端开始，逐项地处理集合，直到处理完所有项。
```
for (objectType varName : collectionReference){
  //Start using objectType(via varName) right away...
}
List<Integer> listOfIntegers = obtainSomehow();
Logger l = Logger.getLogger("Test");
for (Integer i : listOfIntegers) {
  l.info("Integer value is : " + i);
}
```

Java类库中的集合接口和迭代器接口
-------------------
集合类的基本接口是Collection接口。这个接口有两个基本方法：
```
public interface Collection<E>{
    boolean add(E element);
    Iterator<E> iterator();
}
```
如果重复添加一个在集中已有的对象，因为集的特性，这个添加请求就没有实效。

Iterator迭代器对象可用于依次访问对象中的元素。
```
public interface Iterator<E>{
    E next();
    boolean hasNext();
    void remove();
}
```
注：调用remove方法之前没有调用next方法将是不合法的。

如果想查看集合中的所有元素：
```
Collection<String> c = ...;
Iterator<String> iter = c.iterator();
while(iter.hasNext())
{
    String element = iter.next();
    do something with element
}
```
当然用`for each()`方法更简单，它可以和任何实现了Iterable接口的对象一起工作。
```
for(String element : c){
    do something with element
}
```

`contains()`方法的实现：检测集合中是否包含指定元素的泛型。
```
public static<E> boolean contains(Collection<E> c, Object obj)
{
    for (E element : c)
        if (element.equals(obj))
            return ture;
    return false;
}
```
具体的集合
===========
Java库中的具体集合

集合类型  |描述
------   |---------
ArrayList |一种可以动态增长和缩减的索引序列
LinkedList|一种可以在任何位置进行高效地插入和删除操作的有序序列
HashSet  |一种没有重复元素的无序集合
EnumSet  |一种包含枚举类型值的集
LinkedHashSet  |一中可以记住元素插入次序的集
PriorityQueue   |一种允许高效删除元素的集合
HashMap  |一种存储键/值关联的数据结构
TreeMap  |一种键值有序排列的映射表

散列集(HashSet)
--------
先来看一下散列表的定义：
散列表(hash table)是一种可以快速查找所需对象的数据结构。散列表为存储的每一个对象计算一个整数，称之为散列码，这是由对象的实例域产生的。
Java中的散列表使用数组实现，每个列表称之为桶(bucket)。想要查找表中对象的位置，需要先计算散列码，然后与桶的总数取余，得到的结构就是保存这个元素的桶的索引。
是否需要再散列(rehashed)取决于装填因子(load factor)。
下面是一个HashSet的例子：
```
public class SetTest {
    public static void main(String[] args){
        Set<String> words = new HashSet<>();
        int totalTime = 0;

        Scanner in = new Scanner(System.in);
        while (in.hasNext()){
            String word = in.next();
            long callTime = System.currentTimeMillis();
            words.add(word);
            callTime = System.currentTimeMillis() - callTime;
            totalTime += callTime;
        }

        Iterator<String> iter = words.iterator();
        for (int i=1; i <= 5 && iter.hasNext(); i++)
            System.out.println(iter.next());
        System.out.println("...");
        System.out.println(words.size() + "distinct words." + totalTime + " milliseconds.");
    }
}
```

树集(TreeSet)
---------
树集是一个有序集合，当前使用红黑树实现的。
插入元素时实现了Comparable接口
```
public interface Comparable<T>
{
    int compareTo(T other);
}
```

映射表
-----------
Map是一种方便的集合构造，它可将一个对象(键)与另一个对象(值)相关联。仅可包含对象。
Java中有两个映射表的实现：HashMap和TreeMap。这两个类都实现了Map接口。
HashMap对键进行散列，TreeMap用键的整体顺序对元素进行排序，并将其组织为搜索树。HaspMap的速度会快一点，如果不需要排序，一般选择HaspMap。
映射表中的键必须是唯一的，如果对一个键调用两次put方法，第二个值就会取代第一个值。而且映射表并不是一个集合。

同步视图：
```
Map<String, Employee> map = Collections.synchronizedMap(new HashMap<String, Employee>);
```

集合和数组之间的转换
----------
将一个数组转换为集合：
```
String[] values = ...;
HashSet<String> staff = new HashSet<>(Arrays.asList(values));
```
如果将集合转换为数组的话会比较麻烦：
```
String[] values = (String[])staff.toArray();//ERROR!!
```
因为toArray()返回的是Object[]数组，无法改变其类型。可以这样：
```
String[] values = staff.toArray(new String[staff.size()]);
```
参考                                  
------
* [Java集合](https://www.ibm.com/developerworks/cn/java/j-perry-java-collections/index.html)
* Core Java
