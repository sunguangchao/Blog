package db_pools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by 11981 on 2017/12/27.
 */
public class PooledConnection {
    //表示繁忙的标志
    private boolean isBusy = false;
    //真正的数据库连接管道
    private Connection connection;

    public Connection getConnection(){
        return connection;
    }

    public void setConnection(Connection connection){
        this.connection = connection;
    }

    /**
     * 标志我们的管道是否被调用
     * @return
     */
    public boolean isBusy(){
        return isBusy;
    }

    public void setBusy(boolean isBusy){
        this.isBusy = isBusy;
    }
    //构造方法 其他的程序调用它的时候，是要创建并且给它初始化组件
    public PooledConnection(Connection connection, boolean isBusy){
        this.connection = connection;
        this.isBusy = isBusy;
    }

    public void close(){
        this.isBusy = false;
    }

    /**
     * 给我们自定义的管道一个操作数据库数据功能
     * @param sql
     * @return
     */
    public ResultSet queryBysql(String sql){
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return rs;
    }
}
