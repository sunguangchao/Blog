package NIO_IBM;

import java.nio.FloatBuffer;

/**
 * Created by 11981 on 2017/10/21.
 */
public class UseFloatBuffer {
    public static void main(String[] args) throws Exception{
        FloatBuffer buffer = FloatBuffer.allocate(1024);
        for (int i=0; i < buffer.capacity(); ++i){
            float f = (float) Math.sin(((float)i/10) * (2*Math.PI));
            buffer.put(f);
        }
        buffer.flip();
        while (buffer.hasRemaining()){
            float f = buffer.get();
            System.out.println(f);
        }
    }
}
