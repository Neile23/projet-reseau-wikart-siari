package Database.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryPreparer {

    public PreparedStatement prepareInsertUserStatement(Connection conn, String sql, String name) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        return pstmt;
    }

    public PreparedStatement prepareDeleteUserStatement(Connection conn, String sql, String name) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        return pstmt;
    }

    public PreparedStatement prepareSelectAllUsersStatement(Connection conn, String sql) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        return pstmt;
    }

    public PreparedStatement prepareSelectUserIdByNameStatement(Connection conn, String sql, String name) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        return pstmt;
    }

    public PreparedStatement prepareSelectUserNameByIdStatement(Connection conn, String sql, int id) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        return pstmt;
    }

    public PreparedStatement prepareCheckUserExistsStatement(Connection conn, String sql, String name) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        return pstmt;
    }

    public PreparedStatement prepareInsertMessageStatement(Connection conn, String sql, String message, int userId) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, message);
        pstmt.setInt(2, userId);
        return pstmt;
    }

    public PreparedStatement prepareDeleteMessageStatement(Connection conn, String sql, int messageId) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, messageId);
        return pstmt;
    }

    public PreparedStatement prepareSelectAllMessagesStatement(Connection conn, String sql) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        return pstmt;
    }

    public PreparedStatement prepareSelectMessagesByUserIdStatement(Connection conn, String sql, int userId) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);
        return pstmt;
    }

    public PreparedStatement prepareSelectMessageIdsStatement(Connection conn, String sql, String author, String tag,
            int sinceId, int limit) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int index = 1;
        if (author != null) {
            pstmt.setString(index++, author);
        }
        if (tag != null) {
            pstmt.setString(index++, tag.trim());
        }
        if (sinceId >= 0) {
            pstmt.setInt(index++, sinceId);
        }
        pstmt.setInt(index, limit);
        return pstmt;
    }

    public PreparedStatement prepareSelectMessageByIdStatement(Connection conn, String sql, int messageId)
            throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, messageId);
        return pstmt;
    }

    public PreparedStatement prepareSelectUserIdByMessageIdStatement(Connection conn, String sql, int messageId)
            throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, messageId);
        return pstmt;
    }

    public PreparedStatement prepareSelectLastMessageIdStatement(Connection conn, String sql) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(sql);
        return pstmt;
    }

}
