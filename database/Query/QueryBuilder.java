package Database.Query;

public class QueryBuilder {

    public String buildInsertUserQuery() {
        return "INSERT INTO users(name) VALUES(?)";
    }

    public String buildDeleteUserQuery() {
        return "DELETE FROM users WHERE name = ?";
    }

    public String buildSelectAllUsersQuery() {
        return "SELECT id, name FROM users";
    }

    public String buildSelectUserIdByNameQuery() {
        return "SELECT id FROM users WHERE name = ?";
    }

    public String buildSelectUserNameByIdQuery() {
        return "SELECT name FROM users WHERE id = ?";
    }

    public String buildCheckUserExistsQuery() {
        return "SELECT name FROM users WHERE name = ?";
    }

    public String buildInsertMessageQuery() {
        return "INSERT INTO messages(message, user_id) VALUES(?, ?)";
    }

    public String buildDeleteMessageQuery() {
        return "DELETE FROM messages WHERE id = ?";
    }

    public String buildSelectAllMessagesQuery() {
        return "SELECT message, user_id FROM messages";
    }

    public String buildSelectMessagesByUserIdQuery() {
        return "SELECT message FROM messages WHERE user_id = ?";
    }

    public String buildSelectMessageIdsQuery(String author, String tag, int sinceId, int limit) {
        StringBuilder sql = new StringBuilder("SELECT id FROM messages WHERE 1=1");
        if (author != null) {
            sql.append(" AND user_id = (SELECT id FROM users WHERE name = ?)");
        }
        if (tag != null) {
            sql.append(" AND id IN (SELECT message_id FROM tags WHERE tag = ?)");
        }
        if (sinceId >= 0) {
            sql.append(" AND id > ?");
        }
        sql.append(" ORDER BY id DESC LIMIT ?");
        return sql.toString();
    }

    public String buildSelectMessageByIdQuery() {
        return "SELECT message FROM messages WHERE id = ?";
    }

    public String buildSelectUserIdByMessageIdQuery() {
        return "SELECT user_id FROM messages WHERE id = ?";
    }

    public String buildSelectLastMessageIdQuery() {
        return "SELECT * FROM messages ORDER BY id DESC LIMIT 1";
    }

}
