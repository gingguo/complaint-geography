package com.db.jcdb.util;

import java.sql.*;

public class JDBCUtil {
    public static String url="jdbc:MySQL://localhost/reptile?serverTimezone=UTC";
    public static String user="root";
    public static String password="990107ooo@";
    public static String driveName="com.mysql.cj.jdbc.Driver";

//    public static String url = "jdbc:mysql://192.168.43.100:3306/reptile?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=GMT%2B8";
//    public static String user = "1";
//    public static String password = "215";
//    public static String driveName="com.mysql.cj.jdbc.Driver";

//    public static String url = "jdbc:mysql://localhost:3306/information?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=GMT%2B8";
//    public static String user = "root";
//    public static String password = "1313";
//    public static String driveName="com.mysql.cj.jdbc.Driver";

    static{
        try {
            Class.forName(driveName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static Connection getConn(){
        try {
            return DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(Connection conn, Statement st, ResultSet rs){
        if(rs !=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(st !=null){
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
