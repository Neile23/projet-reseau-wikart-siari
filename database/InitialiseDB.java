package database;

import java.sql.*;
import java.util.logging.*;

public class InitialiseDB {
    
    private static final Logger logger = Logger.getLogger(InitialiseDB.class.getName());

    public static void createNewDatabase() {  
        final String url = "jdbc:sqlite:database/database.db";

        try {  
            logger.log(Level.INFO,"Creating database...\n");
            Class.forName("org.sqlite.JDBC");

            Connection conn = DriverManager.getConnection(url);  

            if (conn == null) {  
                 logger.log(Level.SEVERE,"Connection to database failed.\n");
            } 

            logger.log(Level.INFO,"A new database has been created.\n");

            String userTable = "CREATE TABLE IF NOT EXISTS users (\n"
                    + " id integer PRIMARY KEY AUTOINCREMENT,\n"
                    + " name text UNIQUE NOT NULL\n"
                    + ");";
            
            String messagesTable = "CREATE TABLE IF NOT EXISTS messages (\n"
                    + " id integer PRIMARY KEY AUTOINCREMENT,\n"
                    + " message text NOT NULL,\n"
                    + " user_id integer NOT NULL,\n"
                    + " FOREIGN KEY (user_id) REFERENCES users (id)\n"
                    + ");";

            Statement stmt = conn.createStatement();
            stmt.execute(userTable);
            logger.log(Level.INFO,"The table 'users' has been created.\n");
            stmt.execute(messagesTable);
            logger.log(Level.INFO,"The table 'messages' has been created.\n");
            

        } catch (SQLException e) {  
            logger.log(Level.INFO,e.getMessage());  
        } catch (ClassNotFoundException e1) {
            logger.log(Level.INFO,e1.getMessage());
        }
    }  
  
    public static void main(String[] args) {  
        createNewDatabase();  
    }  
}  
