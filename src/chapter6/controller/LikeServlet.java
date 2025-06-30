package chapter6.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import chapter6.beans.User;
import chapter6.dao.LikeDao;
import chapter6.utils.DBUtil;

@WebServlet({ "/api/like", "/api/unlike" })
public class LikeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public LikeServlet() {
		super();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();

		// JSON文字列を構築するためのStringBuilder
		StringBuilder jsonBuilder = new StringBuilder();

		Connection connection = null;
		try {
			connection = DBUtil.getConnection();

			HttpSession session = request.getSession();
			User loginUser = (User) session.getAttribute("loginUser");
			if (loginUser == null) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401 Unauthorized

				// Ajaxリクエストに対して、ログインページへのリダイレクトを指示するヘッダーを追加
				// request.getContextPath() は "/SimpleTwitter" のようなコンテキストパスを返却
				response.setHeader("Location", request.getContextPath() + "/login");

				jsonBuilder.append("{\"success\":false,\"message\":\"ログインしてください。\"}");
				out.print(jsonBuilder.toString());
				out.flush();
				return; // ここで処理を終了
			}
			int userId = loginUser.getId();

			String messageIdStr = request.getParameter("message_id");
			if (messageIdStr == null || messageIdStr.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				jsonBuilder.append("{\"success\":false,\"message\":\"メッセージIDが指定されていません。\"}");
				out.print(jsonBuilder.toString());
				out.flush();
				return;
			}
			int messageId = Integer.parseInt(messageIdStr);

			String requestUri = request.getRequestURI();
			boolean isLike = requestUri.endsWith("/api/like");

			LikeDao likeDao = new LikeDao();

			if (isLike) {
				// いいね処理
				likeDao.create(connection, userId, messageId);
			} else {
				// いいね解除処理
				likeDao.delete(connection, userId, messageId);
			}

			DBUtil.commit(connection);

			int newLikeCount = likeDao.getLikeCount(connection, messageId);
			boolean newIsLikedByCurrentUser = likeDao.isLiked(connection, userId, messageId);

			// JSONを手動で構築
			String jsonOutput = "{\"success\":true,"
					+ "\"new_count\":" + newLikeCount + ","
					+ "\"liked_by_current_user\":" + newIsLikedByCurrentUser + ","
					+ "\"message\":\"処理が成功しました。\"}";
			out.print(jsonOutput);

		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			jsonBuilder.append("{\"success\":false,\"message\":\"不正なメッセージIDです。\"}");
			out.print(jsonBuilder.toString());
			e.printStackTrace();
			DBUtil.rollback(connection);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			jsonBuilder.append("{\"success\":false,\"message\":\"サーバーエラーが発生しました。\"}");
			out.print(jsonBuilder.toString());
			e.printStackTrace();
			DBUtil.rollback(connection);
		} finally {
			DBUtil.close(connection);
			out.flush();
		}
	}
}