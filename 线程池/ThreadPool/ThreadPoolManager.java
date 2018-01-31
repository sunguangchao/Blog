package ThreadPool;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by 11981 on 2018/1/31.
 * ctrl+o
 */
public class ThreadPoolManager implements ThreadPool {
    //线程池中默认线程的个数为5
    private static int workerNum = 5;
    //线程工作数组
    WorkThread[] workThreads;
    private static volatile int executeTaskNumer = 0;//正在被多个线程执行的任务数
    private BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
    private static ThreadPoolManager threadPool;
    private AtomicLong threadNum = new AtomicLong();
    //门面模式的构造方法
    private ThreadPoolManager(){
        this(workerNum);
    }
    private ThreadPoolManager(int newWorkerNum){
        if (newWorkerNum > 0){
            workerNum = newWorkerNum;
        }
        //内存当中开辟了一个连续内存对象
        workThreads = new WorkThread[workerNum];
        for (int i=0; i < workerNum; i++){
            workThreads[i] = new WorkThread();
            workThreads[i].setName("Thread-Worker"+threadNum.incrementAndGet());
            System.out.println("初始化线程数：" + (i+1) + "/" + workerNum + "-----当前线程名称是" + workThreads[i]);
            workThreads[i].start();
        }
    }
    @Override
    public void execute(Runnable task) {
        synchronized (taskQueue){
            try {
                taskQueue.put(task);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            taskQueue.notifyAll();
        }
    }

    @Override
    public void execute(Runnable[] tasks) {
        synchronized (taskQueue){
            for (Runnable task : tasks){
                try {
                    taskQueue.put(task);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            taskQueue.notifyAll();
        }

    }

    @Override
    public void execute(List<Runnable> tasks) {
        synchronized (taskQueue) {
            for (Runnable task : tasks){
                try {
                    taskQueue.put(task);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            taskQueue.notifyAll();
        }
    }

    @Override
    public int getExecuteTaskNumber() {
        return executeTaskNumer;
    }

    @Override
    public int getWaitTaskNumber() {
        return taskQueue.size();
    }

    @Override
    public int getWorkThreadNumber() {
        return workerNum;
    }


    //jdk提供的获取线程池方式
    public static ThreadPool getThreadPool(){
        return getThreadPool(workerNum);
    }
    public static ThreadPool getThreadPool(int newWorkerNum){
        if (newWorkerNum <= 0){
            newWorkerNum = workerNum;
        }
        if (threadPool == null){
            threadPool = new ThreadPoolManager(newWorkerNum);
        }
        return threadPool;
    }

    //Tomcat线程池没办法关闭
    @Override
    public void destory() {
        //不断的监控线程状态
        while (!taskQueue.isEmpty()){
            try {
                Thread.sleep(10);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //任务队列已经为空
        for (int i=0; i < workerNum; i++){
            workThreads[i].stopWorker();
            workThreads[i] = null;
        }
        threadPool = null;
        taskQueue.clear();
    }

    /**
     * 工作线程内部类：工作线程类型属性方法行为的抽象
     * 是辅助线程池框架调用
     */
    private class WorkThread extends Thread{
        private boolean isRunning = true;

        @Override
        public void run() {
            //接收队列当中的任务对象，任务对象为Runnable类型
            Runnable r = null;
            while (isRunning) {
                synchronized (taskQueue) {
                    while (isRunning && taskQueue.isEmpty()) {
                        //队列为空
                        try {
                            taskQueue.wait(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (!taskQueue.isEmpty()) {
                        try {//取出任务
                            r = taskQueue.take();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (r != null) {
                    ((Runnable) r).run();//执行任务
                }
                executeTaskNumer++;
                r = null;
            }
        }
        //停止线程，让该线程自然执行完run方法，自然结束
        public void stopWorker() {
            isRunning = false;
        }
    }

    @Override
    public String toString() {
        return "当前工作的线程数量为：" + workerNum + " 已经完成任务数：" + executeTaskNumer
                + "等待任务数：" + getWaitTaskNumber();
    }
}
