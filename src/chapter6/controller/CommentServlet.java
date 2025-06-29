package chapter6.controller;

import java.io.IOException;
import java.io.PrintWriter; // PrintWriterをインポート
import java.sql.Connection; // Connectionをインポート
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.Comment;
import chapter6.beans.User;
import chapter6.service.CommentService;
import chapter6.utils.DBUtil; // DBUtilをインポート

@WebServlet(urlPatterns = { "/comment" })
public class CommentServlet extends HttpServlet {
	/**
	 * ロガーインスタンスの生成
	 */
	Logger log = Logger.getLogger("twitter");

	/**
	 * デフォルトコンストラクタ
	 * アプリケーションの初期化を実施する。
	 */
	public CommentServlet() {

	}

	// つぶやきの返信 (doGetはJSONを返す必要がないため変更なし。今回は利用しない)
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		response.setContentType("application/json"); // JSONレスポンスを設定
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		StringBuilder jsonBuilder = new StringBuilder(); // JSON構築用

		Connection connection = null; // コネクションを宣言

		try {
			connection = DBUtil.getConnection(); // コネクションを取得

			HttpSession session = request.getSession();
			List<String> errorMessages = new ArrayList<String>();
			String messageIdStr = request.getParameter("message_id"); // messageIdをStringで取得
			String text = request.getParameter("text");

			User user = (User) session.getAttribute("loginUser");
			if (user == null) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
				jsonBuilder.append("{\"success\":false,\"errorMessage\":\"コメントするにはログインしてください。\"}");
				out.print(jsonBuilder.toString());
				out.flush();
				return;
			}
			int userId = user.getId(); // ユーザーIDを取得

			// バリデーション
			if (!isValid(text, errorMessages)) {
				// エラーメッセージをJSONで返す
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
				jsonBuilder.append("{\"success\":false,\"errorMessage\":\"");
				// 複数のエラーメッセージがある場合は結合して返す
				for (String msg : errorMessages) {
					jsonBuilder.append(msg).append("\\n"); // 改行コードを追加
				}
				// 最後の\nを削除（もしあれば）
				if (jsonBuilder.toString().endsWith("\\n")) {
					jsonBuilder.setLength(jsonBuilder.length() - 2); // "\n"の2文字分
				}
				jsonBuilder.append("\"}");
				out.print(jsonBuilder.toString());
				out.flush();
				return;
			}

			int messageId = 0;
			try {
				messageId = Integer.parseInt(messageIdStr);
			} catch (NumberFormatException e) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				jsonBuilder.append("{\"success\":false,\"errorMessage\":\"無効なメッセージIDです。\"}");
				out.print(jsonBuilder.toString());
				out.flush();
				return;
			}

			Comment comment = new Comment();
			comment.setText(text);
			comment.setMessageId(messageId); // 変換後のint型messageIdを使用
			comment.setUserId(userId); // ログインユーザーのIDを使用

			new CommentService().insert(connection, comment);

			DBUtil.commit(connection);

			// 成功レスポンスをJSONで返す
			jsonBuilder.append("{\"success\":true,\"message\":\"コメントが投稿されました！\"}");

		} catch (Exception e) {
			DBUtil.rollback(connection);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonBuilder.append("{\"success\":false,\"errorMessage\":\"コメント投稿中にサーバーで予期せぬエラーが発生しました。\"}");
			log.severe("コメント投稿エラー: " + e.getMessage()); // エラーをログに出力
			e.printStackTrace(); // スタックトレースをコンソールに出力
		} finally {
			DBUtil.close(connection); // コネクションを閉じる
			out.print(jsonBuilder.toString()); // JSONを出力
			out.flush(); // 出力ストリームをフラッシュ
		}
	}

	private boolean isValid(String text, List<String> errorMessages) {
		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		// StringUtils.isBlank は org.apache.commons.lang.StringUtils を使用
		if (StringUtils.isBlank(text)) {
			errorMessages.add("コメントを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("コメントは140文字以下で入力してください");
		}

		return errorMessages.isEmpty(); // エラーメッセージが空ならtrue
	}
}