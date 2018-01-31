package ThreadPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11981 on 2018/1/31.
 */
public class Test {
    public static void main(String[] args) {
        ThreadPool pool = ThreadPoolManager.getThreadPool(6);
        List<Runnable> taskList = new ArrayList<Runnable>();
        for (int i=0; i < 100; i++){
            taskList.add(new Task());
        }
        pool.execute(taskList);
        System.out.println(pool);
        pool.destory();
        System.out.println(pool);
    }
    static class Task implements Runnable{
        private static volatile int i = 1;
        @Override
        public void run() {
            System.out.println("当前的处理线程为" + Thread.currentThread()+ " 正在执行：" + (i++) + "完成");

        }
    }
}
