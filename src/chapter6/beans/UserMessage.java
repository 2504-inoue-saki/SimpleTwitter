package chapter6.beans;

import java.io.Serializable;
import java.util.Date; // java.util.Dateを使用

public class UserMessage implements Serializable {

    private static final long serialVersionUID = 1L; // シリアライズIDの定義

    // messagesテーブル由来のフィールド
    private int id;
    private int userId;
    private String text;
    private Date createdDate;
    private Date updatedDate; // messagesテーブルのupdated_dateに対応

    // usersテーブル由来のフィールド (メッセージ投稿者の情報)
    private String account;
    private String name;

    // いいね機能のためのフィールド
    private int likeCount; // このメッセージのいいね数
    private boolean likedByCurrentUser; // ログインユーザーがこのメッセージにいいね済みか

    // --- コンストラクタ ---
    // デフォルトコンストラクタ (JavaBeansの規約に必須)
    public UserMessage() {
    }

    // --- ゲッターとセッター ---

    // messagesテーブル由来のゲッター/セッター
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    // usersテーブル由来のゲッター/セッター
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // いいね機能のためのゲッター/セッター
    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    // boolean型のゲッターは "is" から始めるのがJavaBeansの慣習
    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

    // --- その他 (オプション) ---
    // デバッグやログ出力に便利なtoStringメソッド
    @Override
    public String toString() {
        return "UserMessage [id=" + id + ", userId=" + userId + ", text=" + text + ", createdDate=" + createdDate
                + ", updatedDate=" + updatedDate + ", account=" + account + ", name=" + name
                + ", likeCount=" + likeCount + ", likedByCurrentUser=" + likedByCurrentUser + "]";
    }
}