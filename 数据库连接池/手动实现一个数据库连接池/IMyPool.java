package db_pools;

/**
 * Created by 11981 on 2017/12/27.
 * 面向接口编程 抽取连接池架构的接口
 * PooledConnection:可复用类型的管道
 */
public interface IMyPool {
    /**
     * 对外提供链接管道
     * @return
     */
    PooledConnection getConnection();

    /**
     * 对内创建链接
     * @param count
     */
    void createConnections(int count);
}
