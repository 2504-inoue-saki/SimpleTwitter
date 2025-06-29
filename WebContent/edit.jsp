<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="pageTitle" value="ツイート編集" scope="request" />
<%-- プロフィールを表示する場合はtrueに設定 --%>
<c:set var="showProfile" value="${true}" scope="request" />

<%-- 共通ヘッダーを読み込む --%>
<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="form-area">
    <form action="edit" method="post" id="editForm">
        <%-- EditServletにmessage_idをhidden状態で渡す --%>
        <input name="message_id" type="hidden" value="${message.id}">
        <textarea name="text" cols="100" rows="5" class="tweet-box">${message.text}</textarea>
        <br />
        <input type="submit" value="更新">（140文字まで）
    </form>
</div>

<%-- 共通フッターを読み込む --%>
<jsp:include page="/WEB-INF/views/layout/footer.jsp" />