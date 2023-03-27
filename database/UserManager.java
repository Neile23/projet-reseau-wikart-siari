package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import Database.Models.User;
import Database.Query.*;

public class UserManager {

    private Connection conn;
    private QueryBuilder queryBuilder;
    private QueryPreparer queryPreparer;
    private QueryExecutor queryExecutor;

    public UserManager(Connection conn) throws SQLException {
        this.conn = conn;
        this.conn.setAutoCommit(true);
        this.queryBuilder = new QueryBuilder();
        this.queryPreparer = new QueryPreparer();
        this.queryExecutor = new QueryExecutor();
    }

    public void addUser(String name) throws SQLException {
        String sql = queryBuilder.buildInsertUserQuery();
        PreparedStatement pstmt = queryPreparer.prepareInsertUserStatement(conn, sql, name);
        queryExecutor.executeUpdate(pstmt);
    }

    public void deleteUser(String name) throws SQLException {
        String sql = queryBuilder.buildDeleteUserQuery();
        PreparedStatement pstmt = queryPreparer.prepareDeleteUserStatement(conn, sql, name);
        queryExecutor.executeUpdate(pstmt);
    }

    public List<User> listUsers() throws SQLException {
        String sql = queryBuilder.buildSelectAllUsersQuery();
        PreparedStatement pstmt = queryPreparer.prepareSelectAllUsersStatement(conn, sql);
        return queryExecutor.executeSelectAllUsersQuery(pstmt);
    }

    public int getUserId(String name) throws SQLException {
        String sql = queryBuilder.buildSelectUserIdByNameQuery();
        PreparedStatement pstmt = queryPreparer.prepareSelectUserIdByNameStatement(conn, sql, name);
        return queryExecutor.executeSelectUserIdByNameQuery(pstmt);
    }

    public String getUserName(int id) throws SQLException {
        String sql = queryBuilder.buildSelectUserNameByIdQuery();
        PreparedStatement pstmt = queryPreparer.prepareSelectUserNameByIdStatement(conn, sql, id);
        return queryExecutor.executeSelectUserNameByIdQuery(pstmt);
    }

    public boolean userExists(String name) throws SQLException {
        String sql = queryBuilder.buildCheckUserExistsQuery();
        PreparedStatement pstmt = queryPreparer.prepareCheckUserExistsStatement(conn, sql, name);
        return queryExecutor.executeCheckUserExistsQuery(pstmt);
    }

}
