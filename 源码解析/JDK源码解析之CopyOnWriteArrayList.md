Java中的Copy-On-Write容器

什么是CopyOnWrite容器？
--------------
自己的理解：
通过CopyOnWriteArrayList可以看出来，它的增删改操作都加了ReentrantLock，读是不加锁的。进行增删改的时候，都是先进行一份拷贝，等操作完成，再将原先数组的引用指向修改之后的数组。

CopyOnWrite的实现原理
--------------------------

![](http://files.jb51.net/file_images/article/201705/2017522154036319.jpg?2017422154045)

知道了CopyOnWrite的实现原理，可以自己写一个CopyOnWriteMap

```java
public class CopyOnWriteMap<K, V> implements Map<K,V>,Cloneable{
    private volatile Map<K, V> internalMap;

    public CopyOnWriteMap(){
        internalMap = new HashMap<K, V>();
    }

    public V put(K key, V value){
        synchronized (this){
            Map<K, V> newMap = new HashMap<K, V>(internalMap);
            V val = newMap.put(key, value);
            internalMap = newMap;
            return val;
        }
    }

    public V get(Object key){
        return internalMap.get(key);
    }

    public void putAll(Map<? extends K, ? extends V> newData){
        synchronized (this){
            Map<K, V> newMap = new HashMap<K, V>(internalMap);
            newMap.putAll(newData);
            internalMap = newMap;
        }
    }
}
```

CopyOnWrite的应用场景
----------------------
CopyOnWrite并发容器用于读多写少的并发场景，如白名单，黑名单，商品类目的访问和更新场景。假如我们有一个搜索网站，用户在这个网站的搜索框中，输入关键字搜索内容，但是某些关键字不允许被搜索。这些不能被搜索的关键字会被放在一个黑名单当中，黑名单每天晚上更新一次。当用户搜索时，会检查当前关键字在不在黑名单当中，如果在，则提示不能搜索。实现代码如下：

```java
kage com.ifeve.book;
import java.util.Map;
import com.ifeve.book.forkjoin.CopyOnWriteMap;
/**
 * 黑名单服务
 *
 * @author fangtengfei
 *
 */
public class BlackListServiceImpl {
    private static CopyOnWriteMap<String, Boolean> blackListMap = new CopyOnWriteMap<String, Boolean>(
            1000);
    public static boolean isBlackList(String id) {
        return blackListMap.get(id) == null ? false : true;
    }
    public static void addBlackList(String id) {
        blackListMap.put(id, Boolean.TRUE);
    }
    /**
     * 批量添加黑名单
     *
     * @param ids
     */
    public static void addBlackList(Map<String,Boolean> ids) {
        blackListMap.putAll(ids);
    }
```
reference:

* http://ifeve.com/java-copy-on-write/
* http://www.cnblogs.com/chengxiao/p/6881974.html