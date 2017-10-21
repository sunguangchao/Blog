package NIO_IBM;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by 11981 on 2017/10/20.
 */
public class FastCopyFile {
    public static void main(String[] args) throws Exception{
        if (args.length < 2){
            System.err.println( "Usage: java FastCopyFile infile outfile" );
            System.exit( 1 );
        }

        String infile = args[0];
        String outfile = args[1];

        FileInputStream fin = new FileInputStream(infile);
        FileOutputStream fout = new FileOutputStream(outfile);

        FileChannel fcin = fin.getChannel();
        FileChannel fcout = fout.getChannel();

        /**
         * allocate():分配生产方式的开销在JVM中
         * allocateDirect():分配生产方式的开销在JVM之外，就是系统级的内存分配
         * 直接字节缓冲区可以通过调用此类的 allocateDirect 工厂方法来创建。
         * 此方法返回的缓冲区进行分配和取消分配所需成本通常高于非直接缓冲区。
         * 直接缓冲区的内容可以驻留在常规的垃圾回收堆之外，因此，它们对应用
         * 程序的内存需求量造成的影响可能并不明显。所以，建议将直接缓冲区主
         * 要分配给那些易受基础系统的本机 I/O 操作影响的大型、持久的缓冲区。
         */
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
        while (true){
            buffer.clear();
            int r = fcin.read(buffer);
            if (r == -1){
                break;
            }
            buffer.flip();
            fcout.write(buffer);
        }
    }
}
