package com.bmstore.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() {
        Connection conn = null;

        try {
            String user = "BMMANAGER";
            String pwd = "1234";
            String url = "jdbc:oracle:thin:@hxlab.co.kr:51521:xe";

            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, user, pwd);
        } catch (ClassNotFoundException ex) {
            System.out.println("DB 드라이버 로딩 실패");
        } catch (SQLException ex) {
            System.out.println("SQL 오류");
        }
        return conn;
    }
}