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
        for (String str : aab){
            ServerSocketChannel listnChannel = ServerSocketChannel.open();
            listnChannel.socket().bind(new InetSocketAddress(Integer.parseInt(str)));
            listnChannel.configureBlocking(false);
            //register selector with channel.the returned key is ignored
            listnChannel.register(selector, SelectionKey.OP_ACCEPT);
        }
        TCPProtocol protocol = new EchoSelectorProtocol(BUFSIZE);
        while (true){
            if (selector.select(TIMEOUT) == 0){
                System.out.println(".");
                continue;
            }

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
                keyIter.remove();
            }
        }

    }
}
