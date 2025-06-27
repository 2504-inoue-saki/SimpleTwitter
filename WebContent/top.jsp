<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="./js/jquery-3.7.1.min.js"></script>
<script src="./js/main.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>簡易Twitter</title>
<link href="./css/style.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div class="main-contents">
		<div class="header">
			<c:if test="${ empty loginUser }">

				<a href="login">ログイン</a>

				<%--今いるリソースから相対位置で「～/signup」というURLにアクセスする--%>
				<a href="signup">登録</a>

			</c:if>
			<c:if test="${ not empty loginUser }">
				<a href="./">ホーム</a>
				<a href="setting">設定</a>
				<a href="logout">ログアウト</a>
			</c:if>
		</div>

		<%-- つぶやきの絞り込み --%>
		<div class="date-filter">
			<form action="./" method="get">
				日付
				<input name="start" type="date" value="${start}" id="start"/>
				～
				<input name="end" type="date" value="${end}" id="end"/>
				<input type="submit" value="絞り込み" id="filter"/>
			</form>
		</div>

		<c:if test="${ not empty loginUser }">
			<div class="profile">
				<div class="name">
					<h2>
						<c:out value="${loginUser.name}" />
					</h2>
				</div>
				<div class="account">
					@
					<c:out value="${loginUser.account}" />
				</div>
				<div class="description">
					<c:out value="${loginUser.description}" />
				</div>
			</div>
		</c:if>
		<c:if test="${ not empty errorMessages }">
			<div class="errorMessages">
				<%--順序なしのリストを作成--%>
				<ul>
					<c:forEach items="${errorMessages}" var="errorMessage">
						<li><c:out value="${errorMessage}" />
					</c:forEach>
				</ul>
			</div>
		</c:if>

		<div class="form-area">
			<c:if test="${ isShowMessageForm }">
				<form action="message" method="post">
					いま、どうしてる？<br />
					<textarea name="text" cols="100" rows="5" class="tweet-box"></textarea>
					<br /> <input type="submit" value="つぶやく">（140文字まで）
				</form>
			</c:if>
		</div>
		<div class="messages">
			<c:forEach items="${messages}" var="message">
				<div class="message">
					<div class="account-name">
						<span class="account">
						<%--"?user_id"がリンクに付くことになる--%>
						<%--各ユーザー毎につぶやきを絞り込み--%>
						<a href="./?user_id=<c:out value="${message.userId}"/> ">
						<c:out value="${message.account}" /></a></span>
						<span class="name">
						<c:out value="${message.name}" /></span>
					</div>
					<div class="text">
						<pre><c:out value="${message.text}" /></pre>
					</div>
					<div class="date">
						<fmt:formatDate value="${message.createdDate}"
							pattern="yyyy/MM/dd HH:mm:ss" />
					</div>

					<%--userIdが一致するつぶやきは編集・削除が可能--%>
					<div class="edit">
						<c:if test="${message.userId == loginUser.id}">
							<%--つぶやきの編集--%>

							<form action="edit" method="get">
								<input name="message_id" type="hidden" value="${message.id}">
								<input type="submit" value="編集" />
							</form>
							<%--つぶやきの削除--%>
							<div class = "delete">
							<form action="deleteMessage" method="post" onsubmit = "return clicked()">
								<input name="message_id" type="hidden" value="${message.id}">
								<input id = "button_delete" type="submit" value="削除" />
							</form>
							</div>
						</c:if>
					</div>
				</div>

				<%--つぶやきの返信--%>
				<div class="comment">
					<div class="eachComment">
						<c:forEach items="${comments}" var="comment">
							<c:if test="${message.id == comment.messageId}">
								<div class="account-name">
									<span class="account"><c:out value="${comment.account}" /></span>
									<span class="name"><c:out value="${comment.name}" /></span>
								</div>
								<div class="test">
									<pre><c:out value="${comment.text}" /></pre>
								</div>
								<div class="date">
									<fmt:formatDate value="${comment.createdDate}" pattern="yyyy/MM/dd HH:mm:ss" />
								</div>
							</c:if>
							</c:forEach>
						</div>
					<div class="form-area">
						<c:if test="${ isShowMessageForm }">
							<form action="comment" method="post">
								<input name="message_id" type="hidden" value="${message.id}">
								返信<br />
								<textarea name="text" cols="100" rows="5" class="reply-box"></textarea>
								<br /> <input type="submit" value="返信">（140文字まで）
							</form>
						</c:if>
					</div>
				</div>
			</c:forEach>
		</div>
		<%--コピーライトの表記は特殊文字を使用--%>
		<div class="copyright">Copyright &copy; Saki Inoue</div>
	</div>
</body>
</html>