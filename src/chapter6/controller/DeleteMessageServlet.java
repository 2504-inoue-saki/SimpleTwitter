package chapter6.controller;

import java.io.IOException;
import java.io.PrintWriter; // 追加
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // 追加

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User; // 追加 (ログインユーザーの情報を取得するため)
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/deleteMessage" })
public class DeleteMessageServlet extends HttpServlet {

	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public DeleteMessageServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		// ★Ajaxリクエストの応答はJSONにする設定
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		StringBuilder jsonBuilder = new StringBuilder();

		HttpSession session = request.getSession();
		User loginUser = (User) session.getAttribute("loginUser");

		// ログインユーザーがいない場合はエラー
		if (loginUser == null) {
		    jsonBuilder.append("{\"success\": false, \"errorMessage\": \"ログインしていません。\"}");
		    out.print(jsonBuilder.toString());
		    out.flush();
		    return;
		}

		String messageIdStr = request.getParameter("message_id");
		Integer id = null;
		if (StringUtils.isBlank(messageIdStr)) {
		    // message_id が空の場合もエラー
		    jsonBuilder.append("{\"success\": false, \"errorMessage\": \"削除対象のメッセージIDが指定されていません。\"}");
		    out.print(jsonBuilder.toString());
		    out.flush();
		    return;
		}

		try {
			id = Integer.parseInt(messageIdStr);
		} catch (NumberFormatException e) {
			log.warning("無効なメッセージIDが指定されました: " + messageIdStr);
			jsonBuilder.append("{\"success\": false, \"errorMessage\": \"無効なメッセージIDです。\"}");
			out.print(jsonBuilder.toString());
			out.flush();
			return;
		}

		try {
		    // MessageService の delete メソッドに、メッセージIDとログインユーザーIDを渡す
		    // これにより、削除権限のチェックをService層で行うことができる
		    MessageService messageService = new MessageService();
		    // deleteメソッドがbooleanを返すように修正されていると仮定 (成功/失敗)
		    boolean deleted = messageService.delete(id, loginUser.getId()); // 仮にdeleteメソッドの引数を変更

		    if (deleted) {
		        jsonBuilder.append("{\"success\": true}");
		    } else {
		        // 削除対象が見つからない、または削除権限がない場合
		        jsonBuilder.append("{\"success\": false, \"errorMessage\": \"ツイートが見つからないか、削除する権限がありません。\"}");
		    }

		} catch (Exception e) {
			log.severe("ツイート削除中にエラーが発生しました: " + e.getMessage());
			e.printStackTrace(); // サーバーログにスタックトレースを出力
			jsonBuilder.append("{\"success\": false, \"errorMessage\": \"ツイート削除中にサーバーで予期せぬエラーが発生しました。\"}");
		} finally {
			// ★最終的にJSONを出力
			out.print(jsonBuilder.toString());
			out.flush();
		}
	}
}