package chapter6.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeDao {

    // いいねを追加する
    public void create(Connection connection, int userId, int messageId) throws SQLException {
        PreparedStatement ps = null;
        try {
            // 重複いいね防止のため、INSERT IGNOREを使用するか、事前に存在チェックを行う
            // ここではシンプルにINSERT INTO。実装によっては重複チェックを入れるべき
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO likes (user_id, message_id) ");
            sql.append("VALUES (?, ?)");

            ps = connection.prepareStatement(sql.toString());
            ps.setInt(1, userId);
            ps.setInt(2, messageId);
            ps.executeUpdate();
        } finally {
            // DBUtil.close(ps); // コネクションは閉じないため、psのみクローズ
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // いいねを削除する
    public void delete(Connection connection, int userId, int messageId) throws SQLException {
        PreparedStatement ps = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM likes ");
            sql.append("WHERE user_id = ? AND message_id = ?");

            ps = connection.prepareStatement(sql.toString());
            ps.setInt(1, userId);
            ps.setInt(2, messageId);
            ps.executeUpdate();
        } finally {
            // DBUtil.close(ps);
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ユーザーが特定のメッセージにいいねしているか確認する
    public boolean isLiked(Connection connection, int userId, int messageId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM likes ");
            sql.append("WHERE user_id = ? AND message_id = ?");

            ps = connection.prepareStatement(sql.toString());
            ps.setInt(1, userId);
            ps.setInt(2, messageId);
            rs = ps.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return true; // いいねが存在する
            }
            return false; // いいねが存在しない
        } finally {
            // DBUtil.close(rs, ps);
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    // 特定のメッセージのいいね総数を取得する
    public int getLikeCount(Connection connection, int messageId) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int count = 0;
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) FROM likes ");
            sql.append("WHERE message_id = ?");

            ps = connection.prepareStatement(sql.toString());
            ps.setInt(1, messageId);
            rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } finally {
            // DBUtil.close(rs, ps);
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
        return count;
    }
}