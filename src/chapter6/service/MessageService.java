package chapter6.service;

import static chapter6.utils.CloseableUtil.close;
import static chapter6.utils.DBUtil.*;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Message;
import chapter6.beans.UserMessage;
import chapter6.dao.MessageDao;
import chapter6.dao.UserMessageDao;
import chapter6.logging.InitApplication;
import chapter6.utils.DBUtil;

public class MessageService {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public MessageService() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	public void insert(Message message) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();
			new MessageDao().insert(connection, message);
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

	public List<UserMessage> select(String userId, String start, String end) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		final int LIMIT_NUM = 1000;

		Connection connection = null;
		try {
			connection = getConnection();
			Integer id = null;
			// 渡されたuserIdをInteger型にキャスト
			if (!StringUtils.isBlank(userId)) {
				id = Integer.parseInt(userId);
			}

			//つぶやきの絞り込み
			// 開始日時の設定
			if (!StringUtils.isBlank(start)) {
				// 半角スペース忘れずに入れる
				start += " 00:00:00";
			} else {
				start = "2020/01/01 00:00:00";
			}
			// 終了日時の設定
			if (!StringUtils.isBlank(end)) {
				end += " 23:59:59";
			} else {
				Date currentDate = new Date();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				end = simpleDateFormat.format(currentDate);
			}

			// UserMessageDaoに渡す
			List<UserMessage> messages = new UserMessageDao().select(connection, id, LIMIT_NUM, start, end);

			commit(connection);

			return messages;
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

	// つぶやきの削除処理
    // ユーザーIDによる権限チェックを追加したdeleteメソッド
    // DAOにメッセージIDとユーザーIDを渡し、削除処理と権限チェックを行わせる
    public boolean delete(Integer messageId, Integer userId) {
        // nullチェック
        if (messageId == null || userId == null) {
            System.err.println("MessageService.delete: messageIdまたはuserIdがnullです。");
            return false;
        }

        // ここに実際のDB削除ロジックを記述
        // 例: DAOを呼び出して、messageIdとuserIdが一致するレコードを削除
        MessageDao messageDao = new MessageDao();
        int affectedRows = messageDao.delete(messageId, userId); // DAOのdeleteメソッドを呼び出し

        // 1件以上削除されたら成功とみなす
        return affectedRows > 0;
    }

	// 編集対象のつぶやきをidで抽出
	public Message select(Integer id) {

		Connection connection = null;
		try {
			connection = getConnection();
			Message message = new MessageDao().select(connection, id);

			commit(connection);

			return message;
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

	// つぶやきの編集処理
	public void update(Message message) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		Connection connection = null;
		try {
			connection = getConnection();
			new MessageDao().update(connection, message);

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