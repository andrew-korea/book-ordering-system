package util;

import java.sql.*;

public class Database {
    private Connection conn;

    public Database() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                "jdbc:mysql://projgw.cse.cuhk.edu.hk:2633/db11",
                "Group11",
                "3170proj"
            );
        } catch(ClassNotFoundException e) {
            System.out.println("[Error]: Java MySQL DB Driver Not Found\n");
            System.exit(0);
        } catch(SQLException e) {
            System.out.println(e);
        }
    }

    public Connection connection() {
        return this.conn;
    }

    public void close() {
        try {
            this.conn.close();
        } catch(SQLException e) {
            System.out.println(e);
        }
    }
}