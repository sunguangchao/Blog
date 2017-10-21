package NIO_IBM;

import java.nio.ByteBuffer;

/**
 * Created by 11981 on 2017/10/20.
 */
public class CreateBuffer {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put((byte)'a');
        buffer.put((byte)'b');
        buffer.put((byte)'c');

        buffer.flip();//将缓冲区由写变为读

        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
    }
}
