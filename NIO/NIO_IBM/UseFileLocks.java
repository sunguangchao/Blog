package NIO_IBM;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Created by 11981 on 2017/10/21.
 */
public class UseFileLocks {
    private static final int start = 10;
    private static final int end = 20;

    public static void main(String[] args) throws Exception{
        RandomAccessFile raf = new RandomAccessFile("E:\\test\\readme.txt", "rw" );
        FileChannel fc = raf.getChannel();
        System.out.println("trying to get lock");
        FileLock lock = fc.lock(start, end, false);
        System.out.println("get lock!");

        System.out.println("pausing");
        try {
            Thread.sleep(3000);
        }catch (InterruptedException e){

        }

        System.out.println("going to release lock");
        lock.release();
        System.out.println("release lock");
        raf.close();

    }
}
