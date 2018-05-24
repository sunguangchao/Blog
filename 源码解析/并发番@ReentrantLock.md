1.ReentrantLock定义
============
1.1 ReentrantLock综述
---------
ReentrantLock是并发包中提供的**独占互斥可重入锁**，与Synchronized的对比就可发现其的拓展性之强：
* 锁机制不同：ReentrantLock 底层实现依赖于AQS(CAS+CLH)，这与Syn的监视器模式截然不同
* 锁获取更加灵活：ReentrantLock 支持响应中断、超时、尝试获取锁，比Syn要灵活的多
* 锁释放形式不同：ReentrantLock 必须显示调用unlock()释放锁，而Syn则会自动释放监视器
* 公平策略支持：ReentrantLock 同时提供公平和非公平策略，以用于同时支持有序或吞吐量更大的执行方式，而Syn本身即非公平锁
* 条件队列支持：ReentrantLock 是基于AQS的独占模式实现，因此还提供对管程形式的条件队列的支持，而Syn则不支持条件队列
* 可重入支持： ReentrantLock 的可重入效果与Syn是一致的，区别是后者会自动释放锁

可重入的定义：

1.2 ReentrantLock实践
--------------
```java
Class X{
    //实例化一个ReentrantLock对象
    private fianl ReentrantLock lock = new ReentrantLock();
    public void m(){
        //阻塞直到获取锁
        lock.lock();
        try{
            //doSomething
        }finally{
            //释放锁-必须每次在finally中执行解锁操作
            lock.unlock();
        }
    }
}
```

2.ReentrantLock数据结构
==========
2.1 类定义
---------
```java
public class ReentrantLock implements Lock, java.io.Serializable
```

2.2 构造器
------------
```java
/**
 * 默认构造器 - 默认使用非公平策略
 * 该构造等价于ReentrantLock(false)
 */
public ReentrantLock(){
    sync = new NonfairSync();
}
/**
 * 可选公平策略构造器 
 * @param fair true：公平策略 false：非公平策略
 */
public ReentrantLock(boolean fair){
    sync = fair ? new FairSync() : new NonfairSync();
}
```
2.3 重要变量
-----------------
```java
/** 同步器提供所有的实现方法，注意sync实例一旦生成就不可变 - 默认非公平 */
private final Sync sync;
```

2.4 锁方法
----------
```java
/**
 * 不响应中断获取锁
 */
public void lock() {
    sync.lock();
}
/**
 * 响应中断获取锁
 */
public void lockInterruptibly() throws InterruptedException {
    sync.acquireInterruptibly(1);
}
/**
 * 尝试获取锁
 */
public boolean tryLock() {
    return sync.nonfairTryAcquire(1);
}
/**
 * 响应超时中断的尝试获取锁
 */
public boolean tryLock(long timeout, TimeUnit unit)
        throws InterruptedException {
    return sync.tryAcquireNanos(1, unit.toNanos(timeout));
}
/**
 * 释放锁
 */
public void unlock() {
    sync.release(1);
}
```

2.5 其他方法
-----------
```java
/**
 * 获取当前线程持有锁的次数
 */
public int getHoldCount() {
    return sync.getHoldCount();
}
/**
 * 判断持有锁的线程是否为当前线程
 * 注意虽然只是名字区别，但这侧面告诉我们可重入锁是独占模式
 */
public boolean isHeldByCurrentThread() {
    return sync.isHeldExclusively();
}
/**
 * 判断锁是否已被持有
 * 该方法只是个监控方法
 */
public boolean isLocked() {
    return sync.isLocked();
}
/**
 * 是否是公平模式 - 默认非公平
 */
public final boolean isFair() {
    return sync instanceof FairSync;
}
/**
 * 判断同步队列是否非空
 * 注意：即使返回true，也不能认为仍有线程想要获取锁
 *      原因在于队列可能存在被取消的节点
 * 该方法只是个监控方法
 */
public final boolean hasQueuedThreads() {
    return sync.hasQueuedThreads();
}
/**
 * 判断指定线程是否在同步队列中
 * 注意：即使返回true，也不能认为该线程想要获取锁
 *      原因在于该线程可能被取消但仍在队列中
 * 该方法只是个监控方法
 */   
public final boolean hasQueuedThread(Thread thread) {
    return sync.isQueued(thread);
} 
/**
 * 获取同步队列中节点数量
 * 注意：该值只是个估计值，因为队列随时会动态变化
 * 该方法只是个监控方法
 */     
public final int getQueueLength() {
    return sync.getQueueLength();
}

```

2.6 条件队列
--------------
```java
/**
 * 支持管程形式的条件队列
 */
public Condition newCondition() {
    return sync.newCondition();
}
/**
 * 判断指定条件队列中是否非空
 * 注意：即使返回true，也不能认为之后的signal操作一定能唤醒节点
 *      原因在于队列中随时可能发生中断或超时事件
 * 该方法只是个监控方法
 */    
public boolean hasWaiters(Condition condition) {
    if (condition == null)
        throw new NullPointerException();
    if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
        throw new IllegalArgumentException("not owner");
    return sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject)condition);
}
/**
 * 获取指定条件队列中节点数量
 * 注意：该值只是个估计值，因为队列随时会动态变化
 * 该方法只是个监控方法
 */ 
public int getWaitQueueLength(Condition condition) {
    if (condition == null)
        throw new NullPointerException();
    if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
        throw new IllegalArgumentException("not owner");
    return sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)condition);
}
```

3.Sync-同步器
============
3.1 类定义
----
```java
/**
 * Base of synchronization control for this lock. Subclassed
 * into fair and nonfair versions below. Uses AQS state to
 * represent the number of holds on the lock.
 *
 * 1.可重入锁的同步控制实现基础
 * 2.其对公平和非公平版本提供基础支持
 * 3.其使用AQS的state字段描述线程持有锁的次数，因此其继承了AQS
 * 注意：锁机制的实现都是基于AQS，因此读者务必先理解一下AQS
 */
abstract static class Sync extends AbstractQueuedSynchronizer
```

3.2 核心方法
------------
### 3.2.1 lock
```java
/**
 * Performs {@link Lock#lock}. The main reason for subclassing
 * is to allow fast path for nonfair version.
 * 
 * 该抽象方法是为了个Lock接口的lock()方法保持一致 
 * 之所以提供给子类实现是为了提供非公平版本快速通过的方式
 */
abstract void lock();
```

###3.2.2 nonfairTryAcquire-important

如果锁未被占用，获取锁时主要做两件事：

* CAS更新状态
* 设置锁关联线程

```java
/**
 * Performs non-fair tryLock.  tryAcquire is implemented in
 * subclasses, but both need nonfair try for trylock method.
 * 尝试获取锁 - 非公平版本
 * @param acquires 想要获取锁的数量
 */
final boolean nonfairTryAcquire(int acquires){
	//1.获取当前线程 - 值得注意的是使用final以确保引用不变 
	final Thread current = Thread.currentThread();
	//2. 获取当前线程状态
	int c = getState();
	//3.当State=0时意味着锁可能还未被占用
	if (c == 0) {
		/**
         * 4.CAS更新锁状态
         * - 由于getState和更新操作之间可能存在并发，因此必须使用CAS
         * - 一旦CAS失败，说明在getState到CAS这段时间内其他锁已经成功获取锁了
         */
		if (compareAndSetState(0, acquires)) {
			//5.除了更新CAS状态，确保线程持有锁的另一个关键步骤就是设置锁关联线程
			setExclusiveOwnerThread(current);
			//6.当成功持有锁后返回true
			return true;
		}
	//7.若当前线程就是持有锁的线程，此时即为可重入情况 
	}else if (current == getExclusiveOwnerThread()) {
		//8.可重入次数累加
		int nextc = c + acquires;
		if (nextc < 0) {
			throw new Error();
		}
	   /**
         * 9.设置锁状态
         * - 值得注意的是此时没有使用CAS，原因在于此时线程已经持有锁，无须用CAS增加不必要的开销
         */
		setState(nextc);
		//10.可重入锁立即返回true
		return true;
	}
	//11.获取锁失败返回false
	return false;
}
```

### 3.2.3  tryRelease
解锁主要做了两件事情：  
1. CAS更新状态(状态值会减少)
2. 解除锁与当前持有线程的关联
  注意：在可重入情况下，在锁还没释放完毕时tryRelease可能返回false
```java
/**
 * 释放锁 - 注意对可重入的支持
 * @param acquires 想要释放锁的数量
 */
protected final boolean tryRelease(int releases) {
    /**
     * 1.减少可重入次数
     * - 值得注意的是此时没有使用CAS，原因在于此时线程已经持有锁，无须用CAS增加不必要的开销
     */
    int c = getState() - releases;
    //2.注意：只有持有锁的线程才能释放锁！！否则直接抛出异常
    if (Thread.currentThread() != getExclusiveOwnerThread())
        throw new IllegalMonitorStateException();
    /**
     * 3.清空锁关联线程 - 即解除锁与当前线程的关联  
     * - 值得注意的是只有可重入锁完全释放完毕才会返回true
     */
    boolean free = false;
    if (c == 0) {
        free = true;
        setExclusiveOwnerThread(null);
    }
    /**
     * 4.设置新的锁状态
     * - 值得注意的是此时没有使用CAS，原因在于此时线程已经持有锁，无须用CAS增加不必要的开销
     */
    setState(c);
    //5.只有可重入锁完全释放完毕才会返回true
    return free;
}
```

###3.2.4 其他重要方法
```java
/**
 * 判断锁是否已被持有
 * 当state=0时说明锁尚未被任何线程锁持有
 */
final boolean isLocked() {
    return getState() != 0;
}
/**
 * 获取持有锁的线程
 * 当state=0时说明锁尚未被任何线程锁持有
 */
final Thread getOwner() {
    return getState() == 0 ? null : getExclusiveOwnerThread();
}
/**
 * 判断持有锁的线程是否为当前线程
 */
protected final boolean isHeldExclusively() {
    return getExclusiveOwnerThread() == Thread.currentThread();
}
/**
 * 获取当前线程持有锁的次数
 * 若当前线程未持有锁，直接返回0
 */
final int getHoldCount() {
    return isHeldExclusively() ? getState() : 0;
}
/**
 * 支持管程形式的条件队列
 * 这也间接说明可重入锁是独占模式锁
 */
final ConditionObject newCondition() {
    return new ConditionObject();
}
```

4.NonfairSync-非公平锁
==========
非公平锁：加锁时无需考虑之前是否有线程等待，直接尝试获取锁，获取失败会自动追加到同步队列队尾。
lock
----------
```java
 /**
 * Performs lock.  Try immediate barge, backing up to normal
 * acquire on failure.
 */
final void lock() {
    /**
     * 1.CAS更新锁状态 0->1
     * 这里算是一种优化：
     *   若锁尚未被任何线程持有时，可以通过CAS快速更新锁状态
     *   一旦成功随后设置锁关联线程即可真正获取到锁
     * 注意：非公平下直接竞争
     */
    if (compareAndSetState(0, 1))
        //2.设置锁关联线程
        setExclusiveOwnerThread(Thread.currentThread());
    else
        //3.调用AQS的aquire方法：处理可重入和锁可能已被占据的情况
        acquire(1);
}
```

tryAcquire
-------------
```java
/**
 * 尝试获取锁 - 非公平版本
 * - 该方法是AQS类的抽象方法tryAcquire()方法的子类实现，主要供AQS使用
 * - 非公平策略会直接调用Sync的nonfairTryAcquire()方法
 * @param acquires 想要获取锁的数量
 */
protected final boolean tryAcquire(int acquires) {
    return nonfairTryAcquire(acquires);
}
```

5.FairSync
=========
lock
--------
```java
/**
 * 获取锁 - 公平版本
 * - 该方法是Sync类的抽象方法lock()方法的子类实现
 * - 该方法直接调用AQS提供的aquire()方法
 */
final void lock() {
    //每次只获取一个资源
    acquire(1);
}
```

tryAcquire
-----------
```java
/**
 * 尝试获取锁 - 公平版本
 * - 该方法是AQS类的抽象方法tryAcquire()方法的子类实现，主要供AQS使用
 * - 只有当递归调用(结束)、没有等待者或是第一个等待者时才可获得锁，
 *   简单来说就是前面没人了，该轮到他了
 * @param acquires 想要获取锁的数量
 */
protected final boolean tryAcquire(int acquires) {
    //1.获取当前线程 - 值得注意的是使用final以确保引用不变 
    final Thread current = Thread.currentThread();
    //2.获取当前线程状态
    int c = getState();
    //3.当State=0时意味着锁可能还未被占用
    if (c == 0) {
       /**
        * 4.同nonfairTryAcquire的重要区别！
        *   同时也是公平策略的实现关键：
        * - 只有当该线程前面已经没有任何等待者时，当前线程才有资格去获取锁
        * - 公平下并不是直接竞争，而是先检查一下在同步队列中它之前还有木有在排队等待的节点
        */
        if (!hasQueuedPredecessors() && compareAndSetState(0, acquires)) {
            setExclusiveOwnerThread(current);
            return true;
        }
    }
    //5.若当前线程就是持有锁的线程，此时即为可重入情况 
    else if (current == getExclusiveOwnerThread()) {
        //6.可重入次数累加
        int nextc = c + acquires;
        if (nextc < 0)
            throw new Error("Maximum lock count exceeded");
        /**
         * 7.设置锁状态
         * - 值得注意的是此时没有使用CAS，原因在于此时线程已经持有锁，无须用CAS增加不必要的开销
         */
        setState(nextc);
        //8.可重入锁立即返回true
        return true;
    }
    //9.获取锁失败返回false
    return false;
}
/**
 * 判断同步队列中是否有比当前线程等待时间更长的线程
 * - 该方法等价于 getFirstQueuedThread() != Thread.currentThread() && hasQueuedThreads()
 * - 该方法专门被设计给公平策略，目的是阻止节点转移
 */
public final boolean hasQueuedPredecessors() {
    Node t = tail; 
    Node h = head;
    Node s;
    //这里也可以体现CLH锁变种的优越性，只要简单的比较head就可知前面是不是还有线程在等待
    return h != t && ((s = h.next) == null || s.thread != Thread.currentThread());
}
```

**小问：ReentrantLock是如何实现公平的？**   
友情小提示：关键在于hasQueuedPredecessors()的使用上面

小答： 有心的读者可能已经发现了，在步骤4时使用了&&操作，即前者必须满足条件后才执行CAS；同时结合tryAquire()在AQS中使用的方式，就很容易知道公平的实现，但为了方便起见，笔者还是用例子简单说一下AQS的实现过程：

**基本要素：**
有一个变量state=0；假设当前线程为A，每当A获取一次锁，state+1;A释放一次锁,state-1;同时锁会设置/清空关联线程;

**基本流程：**
假设当前线程A持有锁，此时state增加并>0；此时线程B尝试获取锁，若(1|N)次执行CAS(0,1)失败，线程会加入同步队列的队尾并被挂起等待；当A完全释放锁时，state减少并=0，同时唤醒同步队列的头节点(假设此时是B)，B被唤醒后会去尝试CAS(0,1)；刚好线程C也尝试去竞争这个锁，下面就有两种实现方式：

**非公平锁实现：**
线程C直接尝试CAS(0,1)，若成功更新成功，则B就获取失败，那么要再次挂起 - 明明是B在C之前就尝试获取锁了，但反而是C先抢到了锁；

**公平锁实现：**
线程C会先检查同步队列中是否有比当前线程等待时间更长的线程，有的话就不执行CAS了，直接进入等待队列并挂起等待，这样B就能获取到锁了.

结论：因此公平和非公平的区别根本在于锁被释放时(state=0时)，还未入队的线程与已入队且刚被唤醒的等待线程(头节点)之间谁先执行CAS，先执行的先成功获取锁嘛，再补充一点，同步队列本身就是FIFO，因此不要弄错谁才是需要比较公平的对象...