package proxy;

/**
 * Created by 11981 on 2017/12/28.
 */
public class WaitTest {
    public static String a = "";

    public static void main(String[] args) throws InterruptedException{
        WaitTest waitTest = new WaitTest();
        TestTask testTask = waitTest.new TestTask();
        Thread t = new Thread(testTask);
        t.start();
        System.out.println("下面线程将睡眠10秒");
        Thread.sleep(10000);
        for (int i=5; i > 0; i--){
            System.out.println("快唤醒挂起的线程----");
            Thread.sleep(3000);

        }
        System.out.println("收到，马上唤醒");
        synchronized (a){
            a.notifyAll();
        }

    }

    class TestTask implements Runnable{

        @Override
        public void run() {
            synchronized (a){
                try {
                    for (int i=10; i > 0; i--){
                        Thread.sleep(1000);
                        System.out.println("我在运行");
                    }
                    a.wait();
                    for (int i=10; i > 0; i--){
                        System.out.println("谢谢唤醒**又开始运行了**");
                    }
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
