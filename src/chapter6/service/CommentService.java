package chapter6.service;

import static chapter6.utils.DBUtil.*; // DBUtilのstatic importは残すが、insert/deleteメソッドでは使わない

import java.sql.Connection; // Connectionをインポート
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import chapter6.beans.Comment;
import chapter6.beans.UserComment;
import chapter6.dao.CommentDao;
import chapter6.dao.UserCommentDao;
import chapter6.utils.DBUtil; // DBUtilをインポート (念のため)

public class CommentService {

	Logger log = Logger.getLogger("twitter");

	public CommentService() {

	}

	public void insert(Connection connection, Comment comment) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		try {

			new CommentDao().insert(connection, comment); // 渡されたconnectionを使用

		} catch (RuntimeException e) {

			log.log(Level.SEVERE, "コメント挿入エラー: " + e.toString(), e);
			throw e; // 例外はServletに伝播させる
		} catch (Error e) {
			log.log(Level.SEVERE, "コメント挿入エラー (Error): " + e.toString(), e);
			throw e; // 例外はServletに伝播させる
		} finally {

		}
	}

	public List<UserComment> select() {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();

			List<UserComment> comments = new UserCommentDao().select(connection);

			commit(connection);

			return comments;
		} catch (RuntimeException e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} catch (Error e) {
			rollback(connection);
			log.log(Level.SEVERE, new Object() {
			}.getClass().getEnclosingClass().getName() + " : " + e.toString(), e);
			throw e;
		} finally {
			DBUtil.close(connection);
		}
	}

	public void delete(Connection connection, int commentId, int userId) {
		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		try {
			new CommentDao().delete(connection, commentId, userId); // DAOに削除を依頼
		} catch (RuntimeException e) {
			log.log(Level.SEVERE, "コメント削除エラー: " + e.toString(), e);
			throw e;
		} catch (Error e) {
			log.log(Level.SEVERE, "コメント削除エラー (Error): " + e.toString(), e);
			throw e;
		}
	}
}