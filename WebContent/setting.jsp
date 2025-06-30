<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%-- ページタイトルを設定 --%>
<% request.setAttribute("pageTitle", "設定"); %>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

		<form action="setting" method="post" class="setting-form">
			<br />
			<input name="id" value="${user.id}" id="id" type="hidden" />
			<label for="name">名前</label>
			<input name="name" value="${user.name}" id="name" />
			<br />
			<label for="account">アカウント名</label>
			<input name="account" value="${user.account}" />
			<br />
			<label for="password">パスワード</label>
			<input name="password" type="password" id="password" />
			<br />
			<label for="email">メールアドレス</label>
			<input name="email" value="${user.email}" id="email" />
			<br />
			<label for="description">説明</label>
			<textarea name="description" cols="35" rows="5" id="description"><c:out value="${user.description}" /></textarea>
			<br />
			<input type="submit" value="更新" />
			<br />
		</form>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />