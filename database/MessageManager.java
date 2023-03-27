package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    public void addMessage(String message, int userId) throws SQLException {
        String sql = queryBuilder.buildInsertMessageQuery();
        PreparedStatement pstmt = queryPreparer.prepareInsertMessageStatement(conn, sql, message, userId);
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
        System.out.println("SQL : " + sql);
        PreparedStatement pstmt = queryPreparer.prepareSelectMessageIdsStatement(conn, sql, author, tag, sinceId,
                limit);
        return queryExecutor.executeSelectMessageIdsQuery(pstmt);
    }

    public String getMessage(int messageId) throws SQLException {
        String sql = queryBuilder.buildSelectMessageByIdQuery();
        PreparedStatement pstmt = queryPreparer.prepareSelectMessageByIdStatement(conn, sql, messageId);
        return queryExecutor.executeSelectMessageByIdQuery(pstmt);
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

}
