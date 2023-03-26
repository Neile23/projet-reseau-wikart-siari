package Database.Models;

public class Message {
    private int id;
    private String message;
    private int userId;

    public Message(int id, String message, int userId) {
        this.id = id;
        this.message = message;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return userId;
    }
}
