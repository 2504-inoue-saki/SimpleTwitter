package chapter6.dao;

import static chapter6.utils.CloseableUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Comment;
import chapter6.exception.SQLRuntimeException;
import chapter6.logging.InitApplication;

public class CommentDao {
	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public CommentDao() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Connection connection, Comment comment) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO comments ( ");
			sql.append("    text, ");
			sql.append("    user_id, ");
			sql.append("    message_id, ");
			sql.append("    created_date, ");
			sql.append("    updated_date ");
			sql.append(") VALUES ( ");
			sql.append("    ?, "); // text
			sql.append("    ?, "); // user_id
			sql.append("    ?, "); // message_id
			sql.append("    CURRENT_TIMESTAMP, "); // created_date
			sql.append("    CURRENT_TIMESTAMP "); // updated_date
			sql.append(")");

			ps = connection.prepareStatement(sql.toString());

			// バインド変数分書く
			ps.setString(1, comment.getText());
			ps.setInt(2, comment.getUserId());
			ps.setInt(3, comment.getMessageId());

			ps.executeUpdate();
		} catch (SQLException e) {
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

	public void delete(Connection connection, int commentId, int userId) {
		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM comments WHERE id = ? AND user_id = ?"); // コメントIDとユーザーIDで確認

			ps = connection.prepareStatement(sql.toString());
			ps.setInt(1, commentId);
			ps.setInt(2, userId); // ログインユーザーのIDと一致するかを確認

			int deletedRows = ps.executeUpdate();
			if (deletedRows == 0) {
				// 削除対象が見つからない、またはユーザーIDが一致しない
				throw new SQLRuntimeException(
						new SQLException("コメントが見つからないか、削除権限がありません。(ID: " + commentId + ", User: " + userId + ")"));
			}

		} catch (SQLException e) {
			log.log(Level.SEVERE, "コメント削除DAOエラー: " + e.toString(), e);
			throw new SQLRuntimeException(e);
		} finally {
			close(ps);
		}
	}

}
