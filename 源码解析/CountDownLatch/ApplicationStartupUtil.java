package countdownlatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by 11981 on 2018/4/10.
 */
public class ApplicationStartupUtil {

    //List of service checkers
    private static List<BaseHealthChecker> _serivces;
    //This latch will be used to wait on
    private static CountDownLatch _latch;

    private ApplicationStartupUtil(){}

    private final static ApplicationStartupUtil INSTANCE = new ApplicationStartupUtil();

    public static ApplicationStartupUtil getInstance(){
        return INSTANCE;
    }


    public static boolean checkExternalSercices() throws Exception{
        //Initialize the latch with number of service checkers
        _latch = new CountDownLatch(3);
        //All add checker in lists
        _serivces = new ArrayList<BaseHealthChecker>();
        _serivces.add(new NetworkHealthChecker(_latch));
        _serivces.add(new CacheHealthChecker(_latch));
        _serivces.add(new DatabaseHealthChecker(_latch));

        Executor executor = Executors.newFixedThreadPool(_serivces.size());
        for (final BaseHealthChecker v : _serivces){
            executor.execute(v);
        }

        _latch.await();

        for (final BaseHealthChecker v : _serivces){
            if (!v.isServiceUp()){
                return false;
            }
        }
        return true;
    }
}
