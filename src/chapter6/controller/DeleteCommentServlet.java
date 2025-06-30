package chapter6.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.service.CommentService; // CommentServiceのimport
import chapter6.utils.DBUtil; // DBUtilのimport

@WebServlet(urlPatterns = { "/commentdelete" }) // main.jsのURLと一致させる
public class DeleteCommentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L; // シリアルバージョンUIDを追加

    Logger log = Logger.getLogger("twitter"); // ロガー

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        log.info(new Object() {
        }.getClass().getEnclosingClass().getName() +
                " : " + new Object() {
                }.getClass().getEnclosingMethod().getName());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        StringBuilder jsonBuilder = new StringBuilder();

        Connection connection = null;

        try {
            connection = DBUtil.getConnection();

            HttpSession session = request.getSession();
            User loginUser = (User) session.getAttribute("loginUser");

            // ログインチェック
            if (loginUser == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonBuilder.append("{\"success\":false,\"errorMessage\":\"コメントを削除するにはログインしてください。\"}");
                out.print(jsonBuilder.toString());
                out.flush();
                return;
            }

            // パラメータ取得
            String commentIdStr = request.getParameter("comment_id");
            // String messageIdStr = request.getParameter("message_id"); // 必要であれば取得

            // コメントIDのバリデーション
            if (StringUtils.isBlank(commentIdStr)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonBuilder.append("{\"success\":false,\"errorMessage\":\"削除するコメントが指定されていません。\"}");
                out.print(jsonBuilder.toString());
                out.flush();
                return;
            }

            int commentId = 0;
            try {
                commentId = Integer.parseInt(commentIdStr);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                jsonBuilder.append("{\"success\":false,\"errorMessage\":\"無効なコメントIDです。\"}");
                out.print(jsonBuilder.toString());
                out.flush();
                return;
            }

            new CommentService().delete(connection, commentId, loginUser.getId()); // 仮にユーザーIDも渡す

            DBUtil.commit(connection); // コミット

            jsonBuilder.append("{\"success\":true,\"message\":\"コメントを削除しました。\"}");

        } catch (Exception e) {
            DBUtil.rollback(connection);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonBuilder.append("{\"success\":false,\"errorMessage\":\"コメント削除中にサーバーエラーが発生しました。\"}");
            log.severe("コメント削除エラー: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBUtil.close(connection);
            out.print(jsonBuilder.toString());
            out.flush();
        }
    }
}