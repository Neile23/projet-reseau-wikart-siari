package Database.Models;

public class Message {
    private int id;
    private String message;
    private int userId;
    private Integer replyToId;
    private boolean republished;

    public Message(int id, String message, int userId, Integer replyToId, boolean republished) {
        this.id = id;
        this.message = message;
        this.userId = userId;
        this.replyToId = replyToId;
        this.republished = republished;
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

    public Integer getReplyToId() {
        return replyToId;
    }

    public boolean isRepublished() {
        return republished;
    }
}
