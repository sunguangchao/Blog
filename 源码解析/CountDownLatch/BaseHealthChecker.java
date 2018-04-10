package countdownlatch;

import java.util.concurrent.CountDownLatch;

/**
 * Created by 11981 on 2018/4/10.
 */
public abstract class BaseHealthChecker implements Runnable{
    private CountDownLatch _latch;
    private String _serviceName;
    private boolean _serviceUp;

    public BaseHealthChecker(String serviceName, CountDownLatch latch){
        super();
        this._latch = latch;
        this._serviceName = serviceName;
        this._serviceUp = false;
    }

    @Override
    public void run(){
        try {
            verifyService();
            _serviceUp = true;
        }catch (Throwable t){
            t.printStackTrace(System.err);
            _serviceUp = true;
        }finally {
            if (_latch != null){
                _latch.countDown();
            }
        }
    }

    public String getServiceName() {
        return _serviceName;
    }

    public boolean isServiceUp(){
        return _serviceUp;
    }

    public abstract void verifyService();
}
