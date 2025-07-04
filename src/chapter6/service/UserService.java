package chapter6.service;

import static chapter6.utils.CloseableUtil.close;
import static chapter6.utils.DBUtil.*;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.dao.UserDao;
import chapter6.logging.InitApplication;
import chapter6.utils.CipherUtil;
import chapter6.utils.DBUtil;

public class UserService {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public UserService() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(User user) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			// パスワード暗号化
			String encPassword = CipherUtil.encrypt(user.getPassword());
			user.setPassword(encPassword);

			//DBにアクセスするための接続情報を変数connectionとして用意
			//実際にDBの操作を行うのはUserDaoの役割であるため、
			//先ほど用意した接続情報（connection）などを引数にinsertメソッドを呼び出す
			connection = getConnection();
			new UserDao().insert(connection, user);
			//DBの操作を確定させるためにコミット
			commit(connection);
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

	public User select(String accountOrEmail, String password) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			// パスワード暗号化
			String encPassword = CipherUtil.encrypt(password);

			connection = getConnection();
			User user = new UserDao().select(connection, accountOrEmail, encPassword);
			commit(connection);

			return user;
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
			close(connection);
		}
	}

	public User select(int userId) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();
			User user = new UserDao().select(connection, userId);
			commit(connection);

			return user;
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
			close(connection);
		}
	}

	// String型のaccountを引数に持つselectメソッド
	public User select(String account) {

		Connection connection = null;
		try {
			connection = getConnection();
			User user = new UserDao().select(connection, account);
			commit(connection);

			return user;
		} catch (RuntimeException e) {
			throw e;
		} catch (Error e) {
			rollback(connection);
			throw e;
		} finally {
			close(connection);
		}
	}

	public void update(User user) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			// パスワード暗号化
			// パスワード未入力、またはスペースのみ入力の場合、暗号化は行わない
			if (!StringUtils.isBlank(user.getPassword())) {
				String encPassword = CipherUtil.encrypt(user.getPassword());
				user.setPassword(encPassword);
			}

			connection = getConnection();
			new UserDao().update(connection, user);
			commit(connection);
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
			close(connection);
		}
	}
}