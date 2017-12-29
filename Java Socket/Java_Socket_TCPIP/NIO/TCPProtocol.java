package NIO1;


import java.io.IOException;
import java.nio.channels.SelectionKey;

/**
 * Created by 11981 on 2017/12/29.
 */
public interface TCPProtocol {
    void handleAccept(SelectionKey key) throws IOException;
    void handleRead(SelectionKey key) throws IOException;
    void handleWrite(SelectionKey key) throws IOException;
}
