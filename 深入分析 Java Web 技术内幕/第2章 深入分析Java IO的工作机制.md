[TOC]
Java的 I/O 类库的基本结构
==============
Java的 I/O 操作类主要分为以下四种：

* 基于字节操作的 I/O 接口：InputStream和OutputStream
* 基于字符操作的 I/O 接口：Wirter和Reader
* 基于磁盘操作的 I/O 接口：File
* 基于网络操作的 I/O 的接口：Socket

数据持久化或网络传输都是以字节进行的，InputStreamReader类是从字节到字符的转化桥梁。

![字符解码类相关结构](http://o90jubpdi.bkt.clouddn.com/%E5%AD%97%E7%AC%A6%E8%A7%A3%E7%A0%81%E7%B1%BB%E7%9B%B8%E5%85%B3%E7%BB%93%E6%9E%84.jpg)

使用如下方式读取一个文件：  

```java
try{
	 StringBuffer str = new StringBuffer();
	 char[] buf = new char[1024];
	 FileReader f = new FileReader("file");
	 while(f.read(buf) > 0)
			 str.append(buf);
	 str.toString();
}catch(IOException e){
}
```
磁盘I/O工作机制
==================
![从磁盘读取文件](http://o90jubpdi.bkt.clouddn.com/%E4%BB%8E%E7%A3%81%E7%9B%98%E8%AF%BB%E5%8F%96%E6%96%87%E4%BB%B6.jpg)
当传入一个文件路径，将会根据这个路径创建一个 File 对象来标识这个文件，然后将会根据这个 File 对象创建真正读取文件的操作对象，这时将会真正创建一个关联真实存在的磁盘文件的文件描述符 FileDescriptor，通过这个对象可以直接控制这个磁盘文件。由于我们需要读取的是字符格式，所以需要 StreamDecoder 类将 byte 解码为 char 格式，至于如何从磁盘驱动器上读取一段数据，由操作系统帮我们完成。


Java序列化技术
-----
Java序列化就是将一个对象转化成一串二进制表示的字节数组，通过保存或转移这些字节数据来达到持久化的目的，需要持久化，对象必须继承`java.io.Serializable`接口

```java
public class Serialize implements Serializable{
	private static final long serialVerisonUID = -463146564812254L;
    public int num = 1390;
    public static void main(String[] args){
        try{
        	 FileOutputStream fos = new FileOutputStream("d:/serialize.dat");
        	 ObjectOutputStream oos = new ObjectOutputStream(fos);
        	 Serialize serialize = new Serialize();
        	 oos.writeObject(serialize);
        	 oos.flush();
        	 oos.close();
        }catch (IOException e){
        	 e.printStackTrace();
        }
    } 
}
```
[Java对象序列化-Serializable](http://www.cnblogs.com/chenfei0801/archive/2013/04/05/3001149.html)

网络I/O工作机制
==================
**TCP状态转化**  （三次握手和四次挥手）

影响网络传输的因素  
* 网络带宽
* 传输距离
* TCP 拥塞控制

Java Socket的工作机制
----------------------
![Java Socket通信](http://o90jubpdi.bkt.clouddn.com/Socket%E9%80%9A%E4%BF%A1.jpg)

主机A的应用程序要能和主机B的应用程序通信，必须通过Socket建立连接，而建立Socket连接必须由底层TCP/IP来建立TCP连接。建立TCP连接需要底层IP来寻址网络中的主机。这样可以通过一个Socket实例来唯一代表一个主机上的应用程序的通信链路了。

建立通信链路
------------
当客户端与服务端通信时，客户端首先创建一个Socket实例，操作系统会为这个Socket分配一个未被使用的本地端口号，并创建一个套接字，在Socket对象创建完成之前，会先进行TCP三次握手，TCP握手协议完成后，Socket实例对象将创建完成，否则将抛出IOException异常。


NIO 的工作方式
============
![NIO相关类图](http://o90jubpdi.bkt.clouddn.com/NIO%E7%9B%B8%E5%85%B3%E7%B1%BB%E5%9B%BE.jpg)
```java
public void selector() throws IOException{
	ByteBuffer buffer = ByteBuffer.allocate(1024);
	Selector selector = Selector.open();
	ServerSocketChannel ssc = ServerSocketChannel.open();
	ssc.configureBlocking(false);//设置为非阻塞方式
	ssc.socket().bind(new InetSocketAddress(8080));
	ssc.register(selector, SelectionKey.OP_ACCEPT);//注册监听的事件
	while (true){
		Set selectedKeys = selector.selectedKeys();//取得所有key集合
		Iterator it = selectedKeys.iterator();
		while (it.hasNext()){
			SelectionKey key = (SelectionKey) it.next();
			if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT){
				ServerSocketChannel ssChannel = (ServerSocketChannel)key.channel();
				SocketChannel sc = ssChannel.accept();//接受到服务器端的请求
				sc.configureBlocking(false);
				sc.register(selector,SelectionKey.OP_READ);
				it.remove();
			}else if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT){
				SocketChannel sc = (SocketChannel) key.channel();
				while (true){
					buffer.clear();
					int n = sc.read(buffer);//读取数据
					if (n < 0){
						break;
					}
					buffer.flip();
				}
				it.remove();
			}
		}        
	}
}
```
调用 Selector 的静态工厂创建一个选择器，创建一个服务端的 Channel 绑定到一个 Socket 对象，并把这个通信信道注册到选择器上，把这个通信信道设置为非阻塞模式。然后就可以调用 Selector 的 selectedKeys 方法来检查已经注册在这个选择器上的所有通信信道是否有需要的事件发生，如果有某个事件发生时，将会返回所有的 SelectionKey，通过这个对象 Channel 方法就可以取得这个通信信道对象从而可以读取通信的数据，而这里读取的数据是 Buffer，这个 Buffer 是我们可以控制的缓冲器。

**服务端如何监听和处理连接请求？**  
一个线程专门负责监听客户端的连接请求，而且是以阻塞的方式进行；另外一个线程专门负责处理请求，这个专门负责处理请求的线程才会真正采用NIO的方式，像Web服务器Tomcat和Jetty都是使用这个处理方式。

下图是描述了基于 NIO 工作方式的 Socket 请求的处理过程：
![](http://o90jubpdi.bkt.clouddn.com/%E5%9F%BA%E4%BA%8E%20NIO%20%E7%9A%84%20Socket%20%E8%AF%B7%E6%B1%82%E7%9A%84%E5%A4%84%E7%90%86%E8%BF%87%E7%A8%8B.jpg)
上图中的 Selector 可以同时监听一组通信信道（Channel）上的 I/O 状态，前提是这个 Selector 要已经注册到这些通信信道中。选择器 Selector 可以调用 select() 方法检查已经注册的通信信道上的是否有 I/O 已经准备好，如果没有至少一个信道 I/O 状态有变化，那么 select 方法会阻塞等待或在超时时间后会返回 0。上图中如果有多个信道有数据，那么将会将这些数据分配到对应的数据 Buffer 中。所以关键的地方是有一个线程来处理所有连接的数据交互，每个连接的数据交互都不是阻塞方式，所以可以同时处理大量的连接请求。

NIO的数据访问方式：
* FileChannel.transferXXX：可以减少数据从内核到用户空间的复制，数据直接在内核空间中移动。
* FileChannel.map：将文件按照一定的大小块映射为内存区域，当程序访问这个内存区域时将直接操作这个文件数据，这种方式省去了数据从内核空间向用户空间复制的损耗。
```java
public static void map(String[] args){
	int BUFFER_SIZE = 1024;
	String filename = "test.db";
	long fileLength = new File(filename).length();
	int bufferCount = 1 + (int)(fileLength/BUFFER_SIZE);
	MappedByteBuffer[] buffers = new MappedByteBuffer[bufferCount];
	long remaining = fileLength;
	for (int i = 0; i < bufferCount; i++) {
		RandomAccessFile file;
		try{
			file = new RandomAccessFile(filename, "r");
			buffers[i] = file.getChannel().map(FileChannel.MapMode.READ_ONLY,
				i*BUFFER_SIZE, (int)Math.min(remaining, BUFFER_SIZE));
		}catch(Exception e){
			e.printStackTrace();
		}
		remaining -= BUFFER_SIZE;
	}
}
```

**同步与异步**  
所谓同步就是一个任务的完成需要依赖另一个任务，只有等待被依赖的任务完成后，依赖的任务才能完成，这是一种可靠的任务序列。要么成功都成功，失败都失败，两个任务的的状态保持一致。而异步是不需要等待被依赖的任务完成，只是通知被依赖的任务要完成什么工作，依赖的任务也立即执行，只要自己完成了整个任务就算完成了。至于被依赖的任务最终是否真正完成，依赖它的任务无法确定，所以它是不可靠的任务序列。我们可以用打电话和发短信来很好的比喻同步与异步操作。
**阻塞与非阻塞**  
阻塞与非阻塞主要是从 CPU 的消耗上来说的，阻塞就是 CPU 停下来等待一个慢的操作完成 CPU 才接着完成其它的事。非阻塞就是在这个慢的操作在执行时 CPU 去干其它别的事，等这个慢的操作完成时，CPU 再接着完成后续的操作。虽然表面上看非阻塞的方式可以明显的提高 CPU 的利用率，但是也带了另外一种后果就是系统的线程切换增加。增加的 CPU 使用时间能不能补偿系统的切换成本需要好好评估。

异步和阻塞的实例：在Cassandra中要查询数据通常会向多个数据节点发送查询命令，但是要检查每个节点返回数据的完整性，就需要一个异步查询同步结果的应用场景：

```java
class AsyncResult implements IAsyncResult{
	private byte[] result_;
	private AtomicBoolean done_ = new AtomicBoolean(false);
	private Lock lock_ = new ReentrantLock();
	private Condition condition_;
	private long stratTime_;
	public AsyncResult(){
		condition_ = lock_.newCondition();//创建一个锁
		stratTime_ = System.currentTimeMillis();
	}
    /* 检查需要的数据是否已经返回，如果没有返回阻塞 */
	public byte[] get(){
		lock_.lock();
		try{
			if(!done_.get()){condition_.await();}
		}catch(InterruptdException ex){
			throw new AssertionError(ex);
		}finally(lock_.unlock()){
			return result_;
		}
	}
    /*** 检查需要的数据是否已经返回 */ 
	public boolean isDone(){return done_.get();}
    /*** 检查在指定的时间内需要的数据是否已经返回，如果没有返回抛出超时异常 */ 
	public byte[] get(long timeout, TimeUnit tu) throws TimeoutException{
		lock_.lock();
		try{    boolean bVal = true;
			try{
				if (!done_.get()) {
					long overall_timeout = timeout - (System.currentTimeMillis() - stratTime_);
					if (overall_timeout > 0) // 设置等待超时的时间
						bVal = condition_.await(overall_timeout, TimeUnit.MILLISECONDS);
					else
						bVal = false;
				}
			}catch(InterruptdException ex){
				throw new AssertionError(ex);
			}
			if (!bVal && !done_.get()) {// 抛出超时异常
				throw new TimeoutException("Operation timed out.");
			}
		}finally{
			lock_.unlock();
		}
		return result_;
	}
    /*** 该函数拱另外一个线程设置要返回的数据，并唤醒在阻塞的线程 */ 
	public void result(Message response){
		try{
			lock_.lock();
			if (!done_.get()) {
				result_ = response.getMessageBody();// 设置返回的数据
				done_.set(true);
				condition_.signal();// 唤醒阻塞的线程
			}
		}finally{
			lock_.lock();
		}
	}
}
```



参考
------------
* 推荐一个IBM的[NIO入门](https://www.ibm.com/developerworks/cn/education/java/j-nio/j-nio.html)，很深刻
* [深入分析 Java I/O 的工作机制](https://www.ibm.com/developerworks/cn/java/j-lo-javaio/)