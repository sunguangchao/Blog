package NIO_IBM;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Created by 11981 on 2017/10/21.
 */
public class UseScatterGather {
    private static final int firstHeaderLength = 2;
    private static final int secondHeaderLength = 4;
    private static final int bodyLength = 6;

    public static void main(String[] args) throws Exception{
        if (args.length != 1){
            System.err.println( "Usage: java UseScatterGather port" );
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(port);
        ssc.socket().bind(address);

        int messageLength = firstHeaderLength + secondHeaderLength + bodyLength;

        ByteBuffer buffers[] = new ByteBuffer[3];
        buffers[0] = ByteBuffer.allocate(firstHeaderLength);
        buffers[1] = ByteBuffer.allocate(secondHeaderLength);
        buffers[2] = ByteBuffer.allocate(bodyLength);

        SocketChannel sc = ssc.accept();

        while (true){
            int bytesRead = 0;
            while (bytesRead < messageLength){
                long r = sc.read(buffers);
                bytesRead += r;

                System.out.println("r " + r);
                for (int i=0; i < buffers.length; ++i){
                    ByteBuffer bb = buffers[i];
                    System.out.println("b "+i+" "+bb.position()+" "+bb.limit());

                }
            }

            for (int i=0; i < buffers.length; ++i){
                ByteBuffer bb = buffers[i];
                bb.flip();
            }

            long bytesWritten = 0;
            while (bytesWritten < messageLength){
                long r = sc.write(buffers);
                bytesWritten += r;
            }

            for (int i=0; i < buffers.length; i++){
                ByteBuffer bb = buffers[i];
                bb.clear();
            }

            System.out.println( bytesRead+" "+bytesWritten+" "+messageLength );

        }


    }
}
