<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%-- ページタイトルを設定 --%>
<% request.setAttribute("pageTitle", "新規ユーザー登録"); %>

<jsp:include page="/WEB-INF/views/layout/header.jsp" />


		<form action="signup" method="post" class="signup-form">
			<br />
			<label for="name">名前</label>
			<input name="name" id="name" />
			<br />
			<label for="account">アカウント名</label>
			<input name="account" id="account" />
			<br />
			<label for="password">パスワード</label>
			<input name="password" type="password" id="password" />
			<br />
			<label for="email">メールアドレス</label>
			<input name="email" id="email" />
			<br />
			<label for="description">説明</label>
			<textarea name="description" cols="35" rows="5" id="description"></textarea>
			<br />
			<input type="submit" value="登録" />
			<br />
		</form>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />