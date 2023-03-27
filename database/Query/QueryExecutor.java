package Database.Query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Database.Models.Message;
import Database.Models.User;

public class QueryExecutor {

    public void executeUpdate(PreparedStatement pstmt) throws SQLException {
        pstmt.executeUpdate();
    }

    public List<Message> executeSelectAllMessagesQuery(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        List<Message> messages = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String message = rs.getString("message");
            int userId = rs.getInt("user_id");
            messages.add(new Message(id, message, userId));
        }
        return messages;
    }

    public List<Message> executeSelectMessagesByUserIdQuery(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        List<Message> messages = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String message = rs.getString("message");
            int userId = rs.getInt("user_id");
            messages.add(new Message(id, message, userId));
        }
        return messages;
    }

    public List<Integer> executeSelectMessageIdsQuery(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        List<Integer> messageIds = new ArrayList<>();
        while (rs.next()) {
            messageIds.add(rs.getInt("id"));
        }
        return messageIds;
    }

    public String executeSelectMessageByIdQuery(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getString("message");
        }
        return null;
    }

    public int executeSelectUserIdByMessageIdQuery(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_id");
        }
        return -1;
    }

    public List<User> executeSelectAllUsersQuery(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            users.add(new User(id, name));
        }
        return users;
    }

    public String executeSelectUserNameByIdQuery(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getString("name");
        }
        return null;
    }

    public boolean executeCheckUserExistsQuery(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        return rs.next();
    }

    public int executeSelectUserIdByNameQuery(PreparedStatement pstmt) throws SQLException {
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("id");
        }
        return -1;
    }

    public int executeSelectLastMessageIdQuery(PreparedStatement pstmt) {
        try {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
