package proxy;



import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by 11981 on 2017/12/28.
 */
public class TestBlockingQueues {
    public static void main(String[] args) {
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(20);
        Thread pro = new Thread(new Producer(queue), "生产者");
        pro.start();
        for (int i=0; i < 10; i++){
            Thread t = new Thread(new Consumer(queue), "消费者 " + i);
            t.start();
        }
    }
}

class Producer implements Runnable{
    BlockingQueue<String> queue;

    public Producer(BlockingQueue<String> queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        int i=0;
        while (true){
            try {
                System.out.println("生产者生产食物，食物编号为：" + i);
                queue.put("食物" + i++);
                Thread.sleep(1000);
            }catch (InterruptedException e){
                System.out.println("生产者被中断");
            }
        }
    }
}

class Consumer implements Runnable{
    BlockingQueue<String> queue;
    public Consumer(BlockingQueue<String> queue){
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true){
            try {
                System.out.println(Thread.currentThread().getName() + "消费"
                        + queue.take());
            }catch (InterruptedException e){
                System.out.println("消费者被中断");
            }
        }
    }
}
