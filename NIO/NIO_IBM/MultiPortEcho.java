package NIO_IBM;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by 11981 on 2017/10/20.
 */
public class MultiPortEcho {
    private int ports[];
    private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);
    public MultiPortEcho(int ports[]) throws IOException{
        this.ports = ports;
        go();
    }

    private void go() throws IOException{
        Selector selector = Selector.open();

        for (int i=0; i < ports.length; ++i){
            //ServerSocketChannel是一个基于通道的socket监听器。
            //用静态的open()方法创建一个新的ServerSocketChannel对象
            ServerSocketChannel ssc = ServerSocketChannel.open();
            //设置为非阻塞
            ssc.configureBlocking(false);
            //将它绑定到给定端口
            ServerSocket ss = ssc.socket();
            InetSocketAddress address = new InetSocketAddress(ports[i]);
            ss.bind(address);

            //将新打开的ServerSocketChannel注册到selector.
            SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Going to listen on "+ ports[i]);
        }

        while (true){
            //The number of keys, possibly zero,whose ready-operation sets were updated
            //select()这个方法会阻塞，直到至少有一个已注册的事件发生
            int num = selector.select();
            Set selectedKeys = selector.selectedKeys();
            Iterator it = selectedKeys.iterator();

            while (it.hasNext()){
                SelectionKey key = (SelectionKey)it.next();
                //readyOps(): return this key's ready-operation set
                //想要监听accept事件
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT){
                    //return the key's channel and transfer to ServerSocketChannel
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);

                    SelectionKey newKey = sc.register(selector, SelectionKey.OP_ACCEPT);
                    it.remove();
                    System.out.println( "Got connection from "+sc );
                }else if((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ){
                    SocketChannel sc = (SocketChannel)key.channel();
                    int bytesEchoed = 0;
                    while (true){
                        echoBuffer.clear();
                        int r = sc.read(echoBuffer);
                        if (r < 0){
                            break;
                        }
                        echoBuffer.flip();
                        sc.write(echoBuffer);
                        bytesEchoed += r;
                    }
                    System.out.println( "Echoed "+bytesEchoed+" from "+sc );

                    it.remove();

                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length<=0) {
            System.err.println( "Usage: java MultiPortEcho port [port port ...]" );
            System.exit( 1 );
        }
        int ports[] = new int[args.length];
        for (int i=0; i < args.length; i++){
            ports[i] = Integer.parseInt(args[i]);
        }
        new MultiPortEcho(ports);
    }
}
