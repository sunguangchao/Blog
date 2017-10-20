package NIO1;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * Created by 11981 on 2017/10/19.
 * come from : http://www.importnew.com/22623.html
 */
public class SelectSockets {
    public static int PORT_NUMBER = 1290;

    public void go(String[] argv) throws Exception{
        int port = PORT_NUMBER;
        if (argv.length > 0){
            //String转化为int
            port = Integer.parseInt(argv[0]);
        }
        System.out.println("Listening on port:" + port);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();//打开一个未绑定的ServerSocketChannel
        ServerSocket serverSocket = serverChannel.socket();//得到一个ServerSocket

        Selector selector = Selector.open();//创建一个Selector供下面使用
        serverSocket.bind(new InetSocketAddress(port));//设置server channel将会监听的端口
        serverChannel.configureBlocking(false);//设置为非阻塞模式
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);//将ServerSocketChannel注册到Selector

        while (true){
            int n = selector.select();
            if (n == 0){
                continue;//nothing to do
            }
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            //在被选择的set中遍历全部的key
            while (it.hasNext()){
                SelectionKey key = (SelectionKey)it.next();
                //判断是否是一个连接到来
                if (key.isAcceptable()){
                    ServerSocketChannel server = (ServerSocketChannel)key.channel();
                    SocketChannel channel = server.accept();
                    registerChannel(selector, channel, SelectionKey.OP_READ);//注册读事件
                    sayHello(channel);//对连接进行处理
                }
                if (key.isReadable()){
                    readDataFromSocket(key);
                }
                //將键从已选择的键的集合中移除
                it.remove();
            }
        }
    }

    protected void registerChannel(Selector selector, SelectableChannel channel, int ops) throws Exception{
        if (channel == null)
            return;
        //设置通道为非阻塞
        channel.configureBlocking(false);
        //将通道注册到选择器上
        channel.register(selector, ops);
    }
    // Use the same byte buffer for all channels. A single thread is
    // servicing all the channels, so no danger of concurrent acccess.
    //对所有的通道使用相同的缓冲区。单线程为所有的通道进行服务，所以并发访问没有风险

    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    private void readDataFromSocket(SelectionKey key) throws Exception{
        SocketChannel socketChannel = (SocketChannel)key.channel();
        int count;
        buffer.clear();//清空缓存区
        // Loop while data is available; channel is nonblocking
        //当可以读到数据时一直循环，通道为非阻塞
        while ((count = socketChannel.read(buffer)) > 0){

            buffer.flip();//缓存区置为可读
            //发送数据，将buffer中的数据读到socketChannel中
            while (buffer.hasRemaining()){
                socketChannel.write(buffer);
            }
            buffer.clear();
        }
        if (count < 0){
            socketChannel.close();
        }
    }

    private void sayHello(SocketChannel channel) throws Exception{
        buffer.clear();//清空缓存区
        buffer.put("Hi there!\r\n".getBytes());//将String转换为Byte
        buffer.flip();//设置为可读
        channel.write(buffer);//将数据从buffer中读出，然后写入channel
    }

    public static void main(String[] argv) {
        try {
            new SelectSockets().go(argv);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
