package NIO_IBM;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by 11981 on 2017/10/21.
 */
public class ReadAndShow {
    public static void main(String[] args) throws Exception{
        FileInputStream fin = new FileInputStream("E:\\test\\readme.txt");
        FileChannel fc = fin.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //read():Reads a sequence of bytes from this channel into the given buffer.
        fc.read(buffer);
        // 字节序列从channel中读出，写入buffer
        // 然后将buffer从写模式转换为读模式
        buffer.flip();
        int i = 0;
        //remain()：返回limit-position：两者之间元素的个数
        while (buffer.remaining() > 0){
            byte b = buffer.get();
            System.out.println("Character " + i + ":" + ((char)b));
            i++;
        }
        //关闭channel
        fin.close();
    }
}
