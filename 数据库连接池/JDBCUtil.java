package ConnPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by 11981 on 2017/12/22.
 */
public class JDBCUtil {
    private static JDBCPool pool = new JDBCPool();
    private static Connection getConnection() throws SQLException{
        return pool.getConnection();
    }

    public static void release(Connection conn, Statement stmt, ResultSet rs){
        if (rs != null){
            try {
                rs.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            rs = null;
        }
        if (stmt != null){
            try {
                stmt.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        if (conn != null){
            try {
                conn.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}
