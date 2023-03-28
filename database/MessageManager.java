package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import Database.Models.Message;
import Database.Query.*;

public class MessageManager {

    private Connection conn;
    private QueryBuilder queryBuilder;
    private QueryPreparer queryPreparer;
    private QueryExecutor queryExecutor;

    public MessageManager(Connection conn) throws SQLException {
        this.conn = conn;
        this.conn.setAutoCommit(true);
        this.queryBuilder = new QueryBuilder();
        this.queryPreparer = new QueryPreparer();
        this.queryExecutor = new QueryExecutor();
    }

    public void addMessage(String message, int userId, Integer replyToId, boolean republished) throws SQLException {
        String sql = queryBuilder.buildInsertMessageQuery();
        PreparedStatement pstmt = queryPreparer.prepareInsertMessageStatement(conn, sql, message, userId, replyToId,
                republished);
        queryExecutor.executeUpdate(pstmt);
    }

    public void deleteMessage(int messageId) throws SQLException {
        String sql = queryBuilder.buildDeleteMessageQuery();
        PreparedStatement pstmt = queryPreparer.prepareDeleteMessageStatement(conn, sql, messageId);
        queryExecutor.executeUpdate(pstmt);
    }

    public List<Message> listMessages() throws SQLException {
        String sql = queryBuilder.buildSelectAllMessagesQuery();
        PreparedStatement pstmt = queryPreparer.prepareSelectAllMessagesStatement(conn, sql);
        return queryExecutor.executeSelectAllMessagesQuery(pstmt);
    }

    public List<Message> listMessages(int userId) throws SQLException {
        String sql = queryBuilder.buildSelectMessagesByUserIdQuery();
        PreparedStatement pstmt = queryPreparer.prepareSelectMessagesByUserIdStatement(conn, sql, userId);
        return queryExecutor.executeSelectMessagesByUserIdQuery(pstmt);
    }

    public List<Integer> getMessageIds(String author, String tag, int sinceId, int limit) throws SQLException {
        String sql = queryBuilder.buildSelectMessageIdsQuery(author, tag, sinceId, limit);
        PreparedStatement pstmt = queryPreparer.prepareSelectMessageIdsStatement(conn, sql, author, tag, sinceId,
                limit);
        return queryExecutor.executeSelectMessageIdsQuery(pstmt);
    }

    public Message getMessage(int messageId) throws SQLException {
        String sql = "SELECT * FROM messages WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, messageId);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int id = rs.getInt("id");
            String message = rs.getString("message");
            int userId = rs.getInt("user_id");
            Integer replyToId = rs.getInt("reply_to_id");
            if (rs.wasNull()) {
                replyToId = null;
            }
            boolean republished = rs.getBoolean("republished");
            return new Message(id, message, userId, replyToId, republished);
        }
        return null;
    }

    public int getUserIdForMessage(int messageId) throws SQLException {
        String sql = queryBuilder.buildSelectUserIdByMessageIdQuery();
        PreparedStatement pstmt = queryPreparer.prepareSelectUserIdByMessageIdStatement(conn, sql, messageId);
        return queryExecutor.executeSelectUserIdByMessageIdQuery(pstmt);
    }

    public int getLastMessageId() throws SQLException {
        String sql = queryBuilder.buildSelectLastMessageIdQuery();
        PreparedStatement pstmt = queryPreparer.prepareSelectLastMessageIdStatement(conn, sql);
        return queryExecutor.executeSelectLastMessageIdQuery(pstmt);
    }

    public void addTag(String tag, int messageId) throws SQLException {
        String sql = "INSERT INTO tags (tag, message_id) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, tag);
        pstmt.setInt(2, messageId);
        pstmt.executeUpdate();
    }

    public List<Message> listReplies(int replyToId) throws SQLException {
        String sql = queryBuilder.buildSelectRepliesByMessageIdQuery();
        PreparedStatement pstmt = queryPreparer.prepareSelectRepliesByMessageIdStatement(conn, sql, replyToId);
        return queryExecutor.executeSelectRepliesByMessageIdQuery(pstmt);
    }

}
