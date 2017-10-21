package NIO_IBM;

import java.nio.ByteBuffer;

/**
 * Created by 11981 on 2017/10/21.
 * slice() 方法根据现有的缓冲区创建一种 子缓冲区 。
 * 也就是说，它创建一个新的缓冲区，新缓冲区与原来的缓冲区的一部分共享数据。
 */
public class SliceBuffer {
    public static void main(String[] args) throws Exception{
        ByteBuffer buffer = ByteBuffer.allocate(10);
        //用数据填充这个缓冲区
        for (int i=0; i < buffer.capacity(); ++i){
            buffer.put((byte) i);
        }
        //对这个缓冲区 分片 ，以创建一个包含槽 3 到槽 6 的子缓冲区
        buffer.position(3);
        buffer.limit(7);

        ByteBuffer slice = buffer.slice();

        //遍历子缓冲区，将里面的数字乘以11
        for (int i=0; i< slice.capacity(); ++i){
            byte b = slice.get(i);
            b *= 11;
            slice.put(i, b);
        }
        //看一下缓冲区的内容
        buffer.position(0);
        buffer.limit(buffer.capacity());

        //将buffer中的字节重复读出
        while (buffer.remaining()>0){
            System.out.println(buffer.get());
        }
    }
}
