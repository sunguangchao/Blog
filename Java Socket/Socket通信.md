端口

* 用于区分不同应用程序
* 端口号范围为0~65535，其中0~1023位系统所保留
* IP地址和端口号组成了所谓的Socket，Socket是网络上运行的程序之间双向通信链路的终结点，是TCP和UDP的基础
* http:80,ftp:20,telnet:23

针对网络通信的不同层次，Java提供的网络功能有四大类：

* InetAddress：用于标识网络上的硬件资源
* URL：同一资源定位符 通过URL可以直接读取或写入网络上的数据
* Sockets：使用TCP协议实现网络通信的Socket相关的类
* Datagram：使用UDP协议，将数据保存在数据报中，通过网络进行通信。

使用URL读取网页的内容

* 通过URL对象的openStream()方法可以得到指定资源的输入流
* 通过输入流可以读取、访问网络上的数据

Socket通信
------------
TCP协议是面向连接、可靠的、有序的，以字节流的方式发送数据
基于TCP协议实现网络通信的类：
* 客户端的Socket类
* 服务器端的ServerSocket类

![](http://o90jubpdi.bkt.clouddn.com/Socket%E9%80%9A%E4%BF%A1%E6%A8%A1%E5%9E%8B.png)

Socket通信实现步骤
* 创建ServerSocket和Socket
* 打开连接到Socket的输入/输出流
* 按照协议对Socket进行读/写操作
* 关闭输入输出流、关闭Socket
```java
package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by 11981 on 2017/6/11.
 * 基于TCP协议的Socket通信，实现用户登陆
 * 服务器端
 */
public class Server {
    public static void main(String[] args) {
        try{
            //1. 创建一个服务器端Socket，即SocketServer，指定绑定的端口，并监听此端口
            ServerSocket serverSocket = new ServerSocket(8888);
            //2. 调用accept()方法开始监听，等待客户端的连接
            System.out.println("服务器即将启动，正在等待客户端的连接");
            Socket socket = serverSocket.accept();
            //3. 获取输入流，并读取客户端信息
            InputStream is = socket.getInputStream();//字节输入流
            InputStreamReader isr = new InputStreamReader(is);//将字节流转化为字符流
            BufferedReader br = new BufferedReader(isr);//为输入流添加缓冲
            String info = null;
            while ((info=br.readLine())!=null){
                System.out.println("我是服务器，客户端说："+info);
            }
            socket.shutdownInput();//关闭输入流

            //4. 获取输出流，响应客户端的请求
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os);//包装为打印流
            pw.write("欢迎您");
            pw.flush();//将缓存输出

            //5. 关闭相关的资源
            pw.close();
            os.close();
            br.close();
            isr.close();
            is.close();
            socket.close();
            serverSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

```
应用多线程来实现服务器与多客户端之间的通信
----------
基本步骤
1. 服务器创建ServerSocket，循环调用accept()等待客户端连接
2. 客户端创建一个socket并请求和服务器端连接
3. 服务器端接受客户端请求，创建socket与客户端建立专线连接
4. 建立连接的两个socket在一个单独的线程上对话
5. 服务器端继续等待新的连接

UDP编程
-------
UDP(用户数据包协议)是无连接、不可靠、无序的  
UDP协议以数据包=报作为数据传输的载体  
在进行数据传输时，首先需要将要传输的数据定义成数据报(Datagram)，在数据报中指明数据所要达到的Socket(主机地址和端口号)，然后再将数据报发送出去。

相关操作类：
* DatagramPacket：表示数据报包
* DatagramSocket：进行端到端通信的类

综合练习：JDBC+IO+Socket编写一个控制台版的“文件上传器”，实现文件上传功能。




