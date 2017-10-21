package NIO_IBM;

import java.nio.ByteBuffer;

/**
 * Created by 11981 on 2017/10/21.
 */
public class TypesInByteBuffer {
    public static void main(String[] args) throws Exception{
        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.putInt(30);
        buffer.putLong(700000000000000L);
        buffer.putDouble(Math.PI);
        buffer.flip();

        System.out.println(buffer.getInt());
        System.out.println(buffer.getLong());
        System.out.println(buffer.getDouble());

    }
}
