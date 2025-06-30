<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%-- ページタイトルを設定 --%>
<% request.setAttribute("pageTitle", "ログイン"); %>

<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<%-- ここにログインフォームのコンテンツのみを記述 --%>
<form action="login" method="post" class="login-form">
	<%-- フォームにクラスを追加 --%>
	<br /> <label for="accountOrEmail">アカウント名かメールアドレス</label> <input
		name="accountOrEmail" id="accountOrEmail" /> <br /> <label
		for="password">パスワード</label> <input name="password" type="password"
		id="password" /> <br /> <input type="submit" value="ログイン" /> <br />
</form>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />