package chapter6.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import chapter6.beans.User;

@WebFilter({"/setting", "/edit"})
public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// セッション領域にログインユーザーの情報があるか？
		User user = (User) httpRequest.getSession().getAttribute("loginUser");

		if (user != null) {
			chain.doFilter(request, response);
		} else {
			// 未ログインで設定画面にいる場合エラー
			List<String> errorMessages = new ArrayList<String>();
			errorMessages.add("ログインしてください");

			HttpSession session = httpRequest.getSession();
			session.setAttribute("errorMessages", errorMessages);
			// ログイン画面に遷移（相対パスであることに注意）
			httpResponse.sendRedirect("./login");
		}
	}

	@Override
	public void init(FilterConfig config) {

	}

	@Override
	public void destroy() {
	}
}
