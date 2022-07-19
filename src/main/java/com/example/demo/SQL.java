package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQL {
    public Connection getConnection(){
        try{
            String dbUrl = "jdbc:oracle:thin:@localhost:1521:xe";
            String userName = "sales_user";
            String password = "mypassword";
            return DriverManager.getConnection(dbUrl, userName, password);
        }catch (Exception exception){
            return null;
        }
    }
}
