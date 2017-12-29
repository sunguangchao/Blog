package NIO1;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by 11981 on 2017/12/29.
 */
public class TCPEchoClientNonblocking {
    public static void main(String[] args) throws Exception{
        String server = "localhost";
        int servPort = 8080;
        byte[] argument = "Hello nonblocking".getBytes();
        //create channel and set to nonblocking
        SocketChannel clntChan = SocketChannel.open();
        clntChan.configureBlocking(false);
        if (!clntChan.connect(new InetSocketAddress(server, servPort))){
            while (!clntChan.finishConnect()){
                System.out.println(".");
            }
        }

        //创建读写缓冲区
        ByteBuffer writeBuf = ByteBuffer.wrap(argument);
        ByteBuffer readBuf = ByteBuffer.allocate(argument.length);

        int totalBytesRcvd = 0;//total bytes received so far
        int bytesRcvd;//bytes received in last read
        while (totalBytesRcvd < argument.length){
            if (writeBuf.hasRemaining()){
                clntChan.write(writeBuf);
            }

            if ((bytesRcvd = clntChan.read(readBuf)) == -1){
                throw new SocketException("Connection closed prematurely");
            }
            totalBytesRcvd += bytesRcvd;
            System.out.println(".");
        }
        System.out.println("Received:" + new String(readBuf.array(), 0, totalBytesRcvd));
        clntChan.close();
    }
}
