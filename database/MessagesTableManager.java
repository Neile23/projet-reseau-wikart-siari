package Database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessagesTableManager {
    
    private final Logger logger = Logger.getLogger(MessagesTableManager.class.getName());
    private final String url = "jdbc:sqlite:database/database.db";
    private Connection conn;
    
    public MessagesTableManager() throws SQLException, ClassNotFoundException {
        conn = DriverManager.getConnection(url);
        Class.forName("org.sqlite.JDBC");
    }
    
    public void addMessage(String message, int userId) throws SQLException {
        String sql = "INSERT INTO messages(message, user_id) VALUES(?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, message);
        pstmt.setInt(2, userId);
        pstmt.executeUpdate();
    }
    
    public void deleteMessage(int messageId) throws SQLException {
        String sql = "DELETE FROM messages WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, messageId);
        pstmt.executeUpdate();
    }
    
    public void listMessages() throws SQLException {
        String sql = "SELECT message, user_id FROM messages";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            logger.log(Level.CONFIG ,"@" + rs.getInt("user_id") + "\t" + rs.getString("message"));
        }
    }

    public void listMessages(int userId) throws SQLException {
        String sql = "SELECT message FROM messages WHERE user_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            logger.log(Level.CONFIG ,"@" + userId + "\t" + rs.getString("message"));
        }
    }
    
    public void close() throws SQLException {
        conn.close();
    }
}
