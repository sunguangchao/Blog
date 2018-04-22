package ways_of_thread;

import java.util.concurrent.Callable;

/**
 * Created by 11981 on 2018/4/22.
 */
public class MyTask implements Callable<Integer> {
    private int upperBounds;

    public MyTask(int upperBounds){
        this.upperBounds = upperBounds;
    }

    @Override
    public Integer call() throws Exception{
        int sum = 0;
        for (int i = 0; i <= upperBounds; i++){
            sum += 1;
        }
        return sum;
    }
}
