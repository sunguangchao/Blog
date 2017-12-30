package db_pools;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by 11981 on 2017/12/29.
 */
public class MyPoolTest {
    private static MyPoolImpl pool = PoolManager.getInstance();

    public synchronized static void selectData(){
        PooledConnection connection = pool.getConnection();
        ResultSet rs = connection.queryBysql("select * from course");
        System.out.println("Thread name: " + Thread.currentThread().getName());
        try {
            while (rs.next()){
//                System.out.print(rs.getString("cno") + "\t\t");
                System.out.print(rs.getString("cname") + "\t\t");
//                System.out.print(rs.getString("tno") + "\t\t");
            }
            rs.close();
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args){
        for (int i=0; i < 1500; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    selectData();
                }
            }).start();
        }
    }
}
