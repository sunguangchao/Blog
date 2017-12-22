package ConnPool;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Created by 11981 on 2017/12/22.
 */
public class JDBCPool implements DataSource {
    private static LinkedList<Connection> listConn = new LinkedList<Connection>();

    static{
        InputStream in = JDBCPool.class.getClassLoader().getResourceAsStream("db.properties");
        Properties prop = new Properties();
        try {
            prop.load(in);
            String driver = prop.getProperty("driver");
            String url = prop.getProperty("url");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            int jdbcPoolInitSize = Integer.parseInt(prop.getProperty("jdbcPoolInitSize"));
            Class.forName(driver);
            for (int i=0; i < jdbcPoolInitSize; i++){
                Connection conn = DriverManager.getConnection(url, username, password);
                System.out.println("获取到了数据库连接：" + conn);
                //将数据库连接加入到listConn队列中，此时listConn队列就是一个存入了数据库连接的连接池
                listConn.add(conn);
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (ClassNotFoundException|SQLException e1 ){
            e1.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException{
        if (listConn.size() > 0){
            final Connection conn = listConn.removeFirst();
            System.out.println(listConn.size());
            //当通过反射来触发，当调用close方法时，将connection放回线程池
            return (Connection) Proxy.newProxyInstance(JDBCPool.class.getClassLoader(),
                    conn.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (!method.getName().equals("close")){
                        return method.invoke(conn, args);
                    }else{
                        listConn.add(conn);
                        System.out.println(conn + "返回给数据库连接池");
                        System.out.println(listConn.size());
                        return null;
                    }
                }
            });
        }else{
            throw new RuntimeException("对不起，数据库忙");
        }
    }


    @Override
    public Connection getConnection(String username, String password){
        return null;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        // TODO Auto-generated method stub

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException{
        return null;
    }



}
