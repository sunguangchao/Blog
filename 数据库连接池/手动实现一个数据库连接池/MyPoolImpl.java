package db_pools;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

/**
 * Created by 11981 on 2017/12/27.
 * ctrl+o
 */
public class MyPoolImpl implements IMyPool {
    //所有配置参数在源码中都有相对应的代码
    private static String driver = null;
    private static String url = null;
    private static String user = null;
    private static String password = null;
    //规范连接池的管道数量参数
    private static int initCount = 3;
    private static int stepSize = 10;
    private static int poolMaxSize = 150;
    private static Vector<PooledConnection> pooledConnections
            = new Vector<PooledConnection>();

    public MyPoolImpl(){
        init();
    }

    private void init(){
        InputStream in = MyPoolImpl.class.getClassLoader().getResourceAsStream("");
        Properties prop = new Properties();
        try {
            prop.load(in);
        }catch (IOException e){
            e.printStackTrace();
        }
        driver = prop.getProperty("jdbcDirver");
        url = prop.getProperty("jdbcurl");
        user = prop.getProperty("userName");
        password = prop.getProperty("password");
        if (Integer.valueOf(prop.getProperty("initCount")) > 0){
            initCount = Integer.valueOf(prop.getProperty("initCount"));
        }else if(Integer.valueOf(prop.getProperty("stepSize")) > 0){
            stepSize = Integer.valueOf(prop.getProperty("stepSize"));
        }else if(Integer.valueOf(prop.getProperty("poolMaxSize")) > 0){
            poolMaxSize = Integer.valueOf(prop.getProperty("poolMaxSize"));
        }

        try {
            Driver dbDriver = (Driver) Class.forName(driver).newInstance();
            DriverManager.registerDriver(dbDriver);

        }catch (ClassNotFoundException | InstantiationException | IllegalAccessException e){
            e.printStackTrace();
        }catch (SQLException e1){
            e1.printStackTrace();
        }
        //监控情况下产生管道
        createConnections(initCount);

    }

    @Override
    public PooledConnection getConnection() {
        if(pooledConnections.size() == 0){
            System.out.println("获取数据库连接管道");
            createConnections(initCount);

        }
        PooledConnection connection = getRealConnection();
        //生产和消费者模式当中
        while (connection == null){
            createConnections(stepSize);
            connection = getConnection();
            try {
                //减少竞争
                Thread.sleep(30);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取真实有效的管道
     * 1.管道未被占用
     * 2.管道没有失效超时
     * @return
     */
    private synchronized PooledConnection getRealConnection(){
        for (PooledConnection conn : pooledConnections){
            if (!conn.isBusy()){
                //进一步判断没有被占有管道的有效性
                Connection connection = conn.getConnection();
                try {
                    //原理：对数据发送一个命令，能否在规定时间内收到回应true
                    if (connection.isValid(2000)){
                        connection = DriverManager.getConnection(url, user, password);
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
                conn.setBusy(true);
                return conn;
            }
        }
        return null;
    }

    @Override
    public void createConnections(int count) {

        if (pooledConnections.size() + count <= poolMaxSize){
            for (int i=0; i < count; i++){
                try {
                    Connection connection = DriverManager.getConnection(url, user, password);
                    PooledConnection pooledConnection = new PooledConnection(connection, false);
                    pooledConnections.add(pooledConnection);
                }catch (SQLException e){
                    e.printStackTrace();
                }

            }
        }else{
            System.out.println("创建失败，参数非法");
        }
    }
}
