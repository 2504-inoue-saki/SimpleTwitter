package chapter6.controller;

import java.io.IOException;
import java.sql.Connection; // Connectionをインポート
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // HttpSessionをインポート

import chapter6.beans.User;
import chapter6.beans.UserComment;
import chapter6.beans.UserMessage;
import chapter6.dao.LikeDao; // LikeDaoをインポート
import chapter6.logging.InitApplication;
import chapter6.service.CommentService;
import chapter6.service.MessageService;
import chapter6.utils.DBUtil;

//アプリケーションのデフォルトのアクセス先としてindex.jspを設定
@WebServlet(urlPatterns = { "/index.jsp" })
public class TopServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public TopServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		boolean isShowMessageForm = false;
		HttpSession session = request.getSession(); // セッションを取得
		User user = (User) session.getAttribute("loginUser"); // ログインユーザーを取得
		if (user != null) {
			isShowMessageForm = true;
		}

		// つぶやきの絞り込み
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		String userIdParam = request.getParameter("user_id"); // パラメータ名変更（user_idとUser変数が紛らわしいため）

		// MessageServiceからメッセージリストを取得
		List<UserMessage> messages = new MessageService().select(userIdParam, start, end);

		Connection connection = null;
		try {
			connection = DBUtil.getConnection(); // DB接続を取得
			LikeDao likeDao = new LikeDao(); // LikeDaoのインスタンスを作成

			// 各メッセージに対して、いいね情報を追加
			for (UserMessage message : messages) {
				// いいね数を取得してセット
				message.setLikeCount(likeDao.getLikeCount(connection, message.getId()));

				// ログインユーザーがいいね済みか確認してセット
				if (user != null) { // ログインしている場合のみチェック
					message.setLikedByCurrentUser(likeDao.isLiked(connection, user.getId(), message.getId()));
				} else {
					message.setLikedByCurrentUser(false); // ログインしていない場合はfalse
				}
			}
		} catch (Throwable e) {
			// 例外が発生した場合はログに出力し、JSPに転送する前にエラーメッセージを設定することも検討
			log.severe("Error fetching like data: " + e.getMessage());
			// エラーハンドリングとして、もしエラーメッセージをユーザーに表示したい場合は、
			// request.setAttribute("errorMessage", "いいね情報の取得中にエラーが発生しました。");
			// のように設定することもできます。
		} finally {
			DBUtil.close(connection); // DB接続をクローズ
		}

		// 返信コメント表示
		List<UserComment> comments = new CommentService().select();

		request.setAttribute("start", start);
		request.setAttribute("end", end);
		request.setAttribute("messages", messages);
		request.setAttribute("comments", comments);
		request.setAttribute("isShowMessageForm", isShowMessageForm);
		// JSPへのフォワード先をtop.jspに明示
		request.getRequestDispatcher("/top.jsp").forward(request, response);
	}
}