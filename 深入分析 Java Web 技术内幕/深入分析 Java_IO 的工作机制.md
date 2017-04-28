Java的 I/O 类库的基本结构
--------------
Java的 I/O 操作类主要分为以下四种：

* 基于字节操作的 I/O 接口：InputStream和OutputStream
* 基于字符操作的 I/O 接口：Wirter和Reader
* 基于磁盘操作的 I/O 接口：File
* 基于网络操作的 I/O 的接口：Socket

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

Java序列化技术
-----
Java序列化就是将一个对象转化成一串二进制表示的字节数组，通过保存或转移这些字节数据来达到持久化的目的，需要持久化，对象必须继承`java.io.Serializable`接口

```
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
--------------
TCP状态转化*  

影响网络传输的因素  

* 网络带宽
* 传输距离
* TCP 拥塞控制

**Java Socket的工作机制：**

主机A的应用程序要能和主机B的应用程序通信，必须通过Socket建立连接，而建立Socket连接必须由底层TCP/IP来建立TCP连接。建立TCP连接需要底层IP来寻址网络中的主机。这样可以通过一个Socket实例来唯一代表一个主机上的应用程序的通信链路了。

当客户端与服务端通信时，客户端首先创建一个Socket实例，操作系统会为这个Socket分配一个未被使用的本地端口号，并创建一个套接字，在Socket对象创建完成之前，会先进行TCP三次握手。

NIO 的工作方式*
----------------

```
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
服务端如何监听和处理连接请求？
一个线程专门负责监听客户端的连接请求，而且是以阻塞的方式进行；另外一个线程专门负责处理请求，这个专门负责处理请求的线程才会真正采用NIO的方式，像Web服务器Tomcat和Jetty都是使用这个处理方式。

NIO的数据访问方式：
* FileChannel.transferXXX：可以减少数据从内核到用户空间的复制，数据直接在内核空间中移动。
* FileChannel.map：将文件按照一定的大小块映射为内存区域，当程序访问这个内存区域时将直接操作这个文件数据，这种方式省去了数据从内核空间向用户空间复制的损耗。

推荐一个IBM的[NIO入门](https://www.ibm.com/developerworks/cn/education/java/j-nio/j-nio.html)，非常深刻
