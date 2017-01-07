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