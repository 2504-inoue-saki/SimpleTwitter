package chapter6.controller;

import java.io.IOException;
import java.io.PrintWriter; // これが必要です
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

// Jackson関連のimportは削除

import chapter6.beans.Message;
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/message" })
public class MessageServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public MessageServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		// ★Ajaxリクエストの応答はJSONにする設定
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		// JSON文字列を構築するためのStringBuilder
		StringBuilder jsonBuilder = new StringBuilder();

		HttpSession session = request.getSession();
		List<String> errorMessages = new ArrayList<String>();

		String text = request.getParameter("text");
		User user = (User) session.getAttribute("loginUser");

		// ログインユーザーがいない場合はエラーとする
		if (user == null) {
			jsonBuilder.append("{\"success\": false, \"errorMessage\": \"ログインしていません。\"}");
			out.print(jsonBuilder.toString());
			out.flush();
			return;
		}

		if (!isValid(text, errorMessages)) {
			// エラーメッセージがある場合、それをJSONで返す
			jsonBuilder.append("{\"success\": false, \"errorMessages\": [");
			for (int i = 0; i < errorMessages.size(); i++) {
				jsonBuilder.append("\"").append(escapeJson(errorMessages.get(i))).append("\"");
				if (i < errorMessages.size() - 1) {
					jsonBuilder.append(", ");
				}
			}
			jsonBuilder.append("]}");
			out.print(jsonBuilder.toString());
			out.flush();
			return;
		}

		try {
			Message message = new Message();
			message.setText(text);
			message.setUserId(user.getId());

			new MessageService().insert(message);

			// ★成功時はJSONで応答
			jsonBuilder.append("{\"success\": true}");
			// 必要に応じて、新しいツイートのHTMLやIDなどを返すことも可能
			// 例: jsonBuilder.append(", \"messageId\": ").append(message.getId());

		} catch (Exception e) {
			log.severe("メッセージ投稿中にエラーが発生しました: " + e.getMessage());
			e.printStackTrace(); // サーバーログにスタックトレースを出力
			jsonBuilder.append("{\"success\": false, \"errorMessage\": \"メッセージ投稿中にサーバーで予期せぬエラーが発生しました。\"}");
		} finally {
			// ★最終的にJSONを出力
			out.print(jsonBuilder.toString());
			out.flush();
		}
	}

	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください。");
		} else if (140 < text.length()) {
			errorMessages.add("メッセージは140文字以下で入力してください。");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}

	// JSON文字列に含めるためのエスケープ処理
	private String escapeJson(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\t", "\\t")
				.replace("\r", "\\r")
				.replace("\b", "\\b")
				.replace("\f", "\\f");
	}
}