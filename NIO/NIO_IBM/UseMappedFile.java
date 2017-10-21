package NIO_IBM;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by 11981 on 2017/10/21.
 */
public class UseMappedFile {
    private static final int start = 0;
    private static final int end = 1024;

    public static void main(String[] args) throws Exception{
        RandomAccessFile raf = new RandomAccessFile("E:\\test\\readme.txt", "rw");
        FileChannel fc = raf.getChannel();
        MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, start, end);

        mbb.put(0, (byte) 97);
        mbb.put(1023, (byte)122);

        raf.close();
    }
}
