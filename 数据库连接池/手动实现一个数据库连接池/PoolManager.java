package db_pools;

/**
 * Created by 11981 on 2017/12/28.
 */
public class PoolManager {
    private static class createPool{
        private static MyPoolImpl poolImpl = new MyPoolImpl();
    }

    /**
     * 内部类单例模式，产生使用对象
     * 仿造类加载器的互斥性表现完美解决线程安全问题
     */
    public static MyPoolImpl getInstance(){
        return createPool.poolImpl;
    }
}
