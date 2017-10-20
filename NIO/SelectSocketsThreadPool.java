package NIO1;




import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 11981 on 2017/10/20.
 * 使用一个线程池来为通道提供服务
 */
public class SelectSocketsThreadPool extends SelectSockets {
    //线程池默认开启的线程数
    private static final int MAX_THREADS = 5;
    private ThreadPool pool = new ThreadPool(MAX_THREADS);

    public static void main(String[] args) {
        try {
            new SelectSocketsThreadPool().go(args);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    protected void readDataFromSocket(SelectionKey key) throws Exception{
        WorkerThread worker = pool.getWorker();
        if (worker == null){
            return;
        }
        worker.serviceChannel(key);
    }

    private class ThreadPool{
        //用于存放线程的队列
        List idle = new LinkedList();
        //初始化线程池
        ThreadPool(int poolSize){
            for (int i=0; i < poolSize; i++){
                WorkerThread thread = new WorkerThread(this);
                thread.setName("Worker" + (i + 1));
                thread.start();
                idle.add(thread);
            }
        }

        WorkerThread getWorker(){
            WorkerThread worker = null;
            synchronized (idle){
                if (idle.size() > 0){
                    worker = (WorkerThread)idle.remove(0);
                }
            }
            return worker;
        }

        void returnWorker(WorkerThread worker){
            synchronized (idle){
                idle.add(worker);
            }
        }

    }

    private class WorkerThread extends Thread{
        private ByteBuffer buffer = ByteBuffer.allocate(1024);
        private ThreadPool pool;
        private SelectionKey key;
        WorkerThread(ThreadPool pool){
            this.pool = pool;
        }

        /**
         * A worker thread class which can drain（排空） channels and echo-back（回显） the input.
         * Each instance is constructed with a reference（参考） to the owning thread pool
         * object. When started, the thread loops forever waiting to be awakened to
         * service the channel associated with a SelectionKey object. The worker is
         * tasked by calling its serviceChannel( ) method with a SelectionKey
         * object. The serviceChannel( ) method stores the key reference in the
         * thread object then calls notify( ) to wake it up. When the channel has
         * been drained, the worker thread returns itself to its parent pool.
         */

        public synchronized void run(){
            System.out.println(this.getName() + " is Ready");
            while (true){
                try {
                    //休眠并且释放对象锁
                    this.wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                    this.interrupt();
                }
                if (key == null){
                    continue;
                }
                System.out.println(this.getName() + "has been awakened");
                try {
                    drainChannel(key);
                }catch (Exception e){
                    System.out.println("Caught '" + e + "' closing channel");
                    try {
                        key.channel().close();
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }
                    key.selector().wakeup();
                }
                key = null;
                this.pool.returnWorker(this);
            }
        }

        /**
         * Called to initiate a unit of work by this worker thread on the
         * provided SelectionKey object. This method is synchronized, as is the
         * run( ) method, so only one key can be serviced at a given time.
         * Before waking the worker thread, and before returning to the main
         * selection loop, this key's interest set is updated to remove OP_READ.
         * This will cause the selector to ignore read-readiness for this
         * channel while the worker thread is servicing it.
         * 通过一个被提供SelectionKey对象的工作线程来初始化一个工作集合，这个方法是同步的，所以
         * 里面的run方法只有一个key能被服务在同一个时间，在唤醒工作线程和返回到主循环之前，这个key的
         * 感兴趣的集合被更新来删除OP_READ，这将会引起工作线程在提供服务的时候选择器会忽略读就绪的通道
         */
        synchronized void serviceChannel(SelectionKey key){
            this.key = key;
            key.interestOps(key.interestOps() & (~SelectionKey.OP_READ));//取消一个read事件
            this.notify();
        }

        void drainChannel(SelectionKey key) throws Exception{
            SocketChannel channel = (SocketChannel) key.channel();
            int count;
            buffer.clear();
            while ((count = channel.read(buffer))> 0){
                buffer.flip();
                while (buffer.hasRemaining()){
                    channel.write(buffer);
                }
                buffer.clear();
            }
            if (count < 0){
                channel.close();
                return;
            }
            key.interestOps(key.interestOps() | SelectionKey.OP_READ);//update一个read事件
            key.selector().wakeup();
        }
    }


}
