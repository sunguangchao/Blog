package ThreadPool;

import java.util.List;

/**
 * Created by 11981 on 2018/1/31.
 */
public interface ThreadPool {
    void execute(Runnable task);
    void execute(Runnable[] tasks);
    void execute(List<Runnable> tasks);
    //返回可以执行任务的个数
    int getExecuteTaskNumber();
    //返回任务队列的长度，即还没处理的任务的个数
    int getWaitTaskNumber();
    //返回工作线程的个数
    int getWorkThreadNumber();
    //关闭线程池
    void destory();

}
