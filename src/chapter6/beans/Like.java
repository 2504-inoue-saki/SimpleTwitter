package chapter6.beans;

import java.sql.Date;

public class Like {
    private int id;
    private int userId;
    private int messageId;
    private Date createdAt;


    // ゲッターメソッド
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getMessageId() {
        return messageId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    // セッターメソッド
    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}
