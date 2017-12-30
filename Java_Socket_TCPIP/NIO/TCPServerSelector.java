package NIO1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * Created by 11981 on 2017/12/29.
 */
public class TCPServerSelector {
    private static final int BUFSIZE = 256;
    private static final int TIMEOUT = 3000;

    public static void main(String[] args) throws IOException{
        Selector selector = Selector.open();
        String[] aab = {"8080","8081"};
        //为每个端口创建一个ServerSocketChannel
        for (String str : aab){
            ServerSocketChannel listnChannel = ServerSocketChannel.open();
            //获得底层的ServerSocket，并以端口号作为参数调用其bind()方法
            listnChannel.socket().bind(new InetSocketAddress(Integer.parseInt(str)));
            listnChannel.configureBlocking(false);
            //register selector with channel.the returned key is ignored
            //为信道选择注册器，并指出该信道可以进行"accept"操作
            listnChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        //create a handler that will implement the protocal
        TCPProtocol protocol = new EchoSelectorProtocol(BUFSIZE);
        //反复循环，等待I/O调用操作器
        while (true){
            //select()方法阻塞等待，直到有准备好I/O操作的信道，或者直到发生了超时。该方法将返回准备好的信道数
            if (selector.select(TIMEOUT) == 0){
                System.out.println(".");//超时了，打印一个点来标记迭代次数
                continue;
            }
            //获取所选择的键集

            /**
             * 调用selectedKeys()方法返回一个Set实例，并从中获取一个Iterator。该集合
             * 包含了每个准备好某一I/O操作的信道的SelectionKey(在注册时创建)。
             */
            Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
            while (keyIter.hasNext()){
                SelectionKey key = keyIter.next();
                if (key.isAcceptable()){
                    protocol.handleAccept(key);
                }
                if (key.isReadable()){
                    protocol.handleRead(key);
                }
                if (key.isValid() && key.isWritable()){
                    protocol.handleWrite(key);
                }
                keyIter.remove();//最后将该键移除
            }
        }

    }
}
