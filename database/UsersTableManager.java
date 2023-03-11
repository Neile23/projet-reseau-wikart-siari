package Database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsersTableManager {
    private final Logger logger = Logger.getLogger(UsersTableManager.class.getName());
    private final String url = "jdbc:sqlite:database/database.db";
    private Connection conn;
    
    public UsersTableManager() throws SQLException, ClassNotFoundException {
        conn = DriverManager.getConnection(url);
        Class.forName("org.sqlite.JDBC");
    }

    public void addUser(String name) throws SQLException {
        String sql = "INSERT INTO users(name) VALUES(?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.executeUpdate();
    }

    public void deleteUser(String name) throws SQLException {
        String sql = "DELETE FROM users WHERE name = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.executeUpdate();
    }

    public void listUsers() throws SQLException {
        String sql = "SELECT id, name FROM users";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            logger.log(Level.CONFIG ,rs.getInt("id") + "\t" + rs.getString("name"));
        }
    }

    public int getUserId(String name) throws SQLException {
        String sql = "SELECT id FROM users WHERE name = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        return rs.getInt("id");
    }

    public String getUserName(int id) throws SQLException {
        String sql = "SELECT name FROM users WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();
        return rs.getString("name");
    }

    public boolean userExists(String name) throws SQLException {
        String sql = "SELECT name FROM users WHERE name = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        return rs.next();
    }

    public void close() throws SQLException {
        conn.close();
    }
    
}
