package chapter6.controller;

import java.io.IOException;
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

import chapter6.beans.Message;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {
	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public EditServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	//つぶやきの編集
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();
		String messageId = request.getParameter("message_id");
		Message targetMessage = null;

		// messageIdは数字のみ許可
		if ((messageId != null) && messageId.matches("^[0-9]+$")) {
			Integer id = Integer.parseInt(messageId);
			targetMessage = new MessageService().select(id);
		}

		if (targetMessage == null) {
			errorMessages.add("不正なパラメータが入力されました");

			if (errorMessages.size() != 0) {
				HttpSession session = request.getSession();
				session.setAttribute("errorMessages", errorMessages);
				response.sendRedirect("./");
				return;
			}
		}
		// 受け取った情報をedit.jspに渡す
		request.setAttribute("message", targetMessage);

		request.getRequestDispatcher("/edit.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();
		Message message = new Message();
		message.setId(Integer.parseInt(request.getParameter("message_id")));
		message.setText(request.getParameter("text"));

		// isValidがfalseの場合、エラー処理を行う
		if (!isValid(message.getText(), errorMessages)) {
			request.setAttribute("errorMessages", errorMessages);
			request.setAttribute("message", message);
			request.getRequestDispatcher("edit.jsp").forward(request, response);
			return;
		}
		// エラーチェックを終えてからMessageServiceを呼ぶ
		new MessageService().update(message);

		response.sendRedirect("./");

	}

	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}

}