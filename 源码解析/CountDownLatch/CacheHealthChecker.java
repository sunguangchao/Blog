package countdownlatch;


import java.util.concurrent.CountDownLatch;

/**
 * Created by 11981 on 2018/4/10.
 */
public class CacheHealthChecker extends BaseHealthChecker{
    public CacheHealthChecker(CountDownLatch latch){
        super("Cache Service", latch);
    }

    @Override
    public void verifyService(){
        System.out.println("Checking " + this.getServiceName());
        try {
            Thread.sleep(6000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println(this.getServiceName() + "is UP");
    }
}
