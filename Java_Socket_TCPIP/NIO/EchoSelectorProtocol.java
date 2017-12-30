package NIO1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by 11981 on 2017/12/29.
 */
public class EchoSelectorProtocol implements TCPProtocol{
    private int bufSize;

    public EchoSelectorProtocol(int bufSize){
        this.bufSize = bufSize;
    }
    @Override
    public void handleAccept(SelectionKey key) throws IOException {

        //从键中获取信道，并接受连接
        //channel()方法返回注册时用来创建键的channel。
        SocketChannel clntChan = ((ServerSocketChannel)key.channel()).accept();
        clntChan.configureBlocking(false);
        clntChan.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufSize));
    }

    @Override
    public void handleRead(SelectionKey key) throws IOException {

        SocketChannel clntChan = (SocketChannel)key.channel();
        //获取键关联的缓冲区
        ByteBuffer buf = (ByteBuffer)key.attachment();
        long bytesRead = clntChan.read(buf);
        //从键中读取数据
        if (bytesRead == -1){
            clntChan.close();
        }else if (bytesRead > 0){//保留了信道的可读操作
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }
    }

    @Override
    public void handleWrite(SelectionKey key) throws IOException {
        //附加到SelectionKey上的ByteBuffer包含了之前从信道中读取的数据。
        ByteBuffer buf = (ByteBuffer) key.attachment();
        buf.flip();//准备缓冲区的写操作
        SocketChannel clntChan = (SocketChannel) key.channel();//获取信道
        clntChan.write(buf);//向信道写数据
        if (!buf.hasRemaining()){//如果缓冲区为空，则标记为不再写数据。
            key.interestOps(SelectionKey.OP_READ);
        }
        buf.compact();//压缩缓冲区

    }
}
