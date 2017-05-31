package dao;

import entity.Items;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by 11981 on 2017/5/30.
 */
public class ItemsDAO {
    static final String DB_DRIVER = "com.mysql.jdbc.Driver";

    static final String DB_URL = "jdbc:mysql://localhost/sampledb?" +
            "useUnicode=true&characterEncoding=utf-8&useSSL=false";

    static final String USER = "root";

    static final String PASSWORD = "root";

    public ArrayList<Items> getAllItems() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Items> list = new ArrayList<Items>();
        try {
            Class.forName(DB_DRIVER);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String sql = "SELECT * FROM items";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                Items item = new Items();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setCity(rs.getString("city"));
                item.setNumber(rs.getInt("number"));
                item.setPrice(rs.getInt("price"));
                item.setPicture(rs.getString("picture"));
                list.add(item);
            }
            return list;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try{
                if (stmt != null){
                    stmt.close();
                }
            }catch (SQLException se){
                se.printStackTrace();
            }

            try{
                if (conn != null){
                    conn.close();
                }
            }catch (SQLException se){
                se.printStackTrace();
            }
        }
    }

    public Items getItemsById(int id){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            String sql = "SELECT * FROM items WHERE id=?;";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()){
                Items item = new Items();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setCity(rs.getString("city"));
                item.setNumber(rs.getInt("number"));
                item.setPrice(rs.getInt("price"));
                item.setPicture(rs.getString("picture"));
                return item;
            }else{
                return null;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }finally {
            try{
                if (stmt != null){
                    stmt.close();
                }
            }catch (SQLException se){
                se.printStackTrace();
            }

            try{
                if (conn != null){
                    conn.close();
                }
            }catch (SQLException se){
                se.printStackTrace();
            }
        }
    }

    public ArrayList<Items> getViewList(String list){
        System.out.println("list:"+list);
        ArrayList<Items> itemlist = new ArrayList<Items>();
        int iCount = 5;
        if (list != null && list.length() > 0){
            String[] arr = list.split("#");
            int N = arr.length;
            System.out.println("arr.length="+N);
            if (N > 5){
                for (int i = N-1; i >= N-iCount; i--){
                    itemlist.add(getItemsById(Integer.parseInt(arr[i])));
                }
            }else{
                for (int i = N-1; i >= 0; i--){
                    itemlist.add(getItemsById(Integer.parseInt(arr[i])));
                }

            }
            return itemlist;
        }
        else
        {
            return null;
        }
    }
}
