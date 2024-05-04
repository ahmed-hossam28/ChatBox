package org.example.chatbox.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL implements DBConnection{
   String url = "jdbc:mysql://localhost:3306";
   String username;
   String password;
   String dbname;
 public MySQL(String username,String password, String dbname){
        url = url+'/'+dbname;
       this.username = username;
       this.password = password;
       this.dbname = dbname;
    }
    @Override
    public Connection connect() {
        try {
            return DriverManager.getConnection(url, username, password);
        }
        catch (SQLException e){//handle
            System.out.println(e.getMessage());
        }

        return null;
    }
}
