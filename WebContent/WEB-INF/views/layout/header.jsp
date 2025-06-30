<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <%-- ページタイトルは各JSPで設定できるよう、ここではデフォルト値を設定 --%>
    <title><c:out value="${pageTitle}" default="簡易Twitter" /></title>
    <link href="./css/style.css" rel="stylesheet" type="text/css">
</head>
<body>
    <%-- ここからヘッダーとプロファイル表示の部分 --%>
    <div class="main-contents">
        <div class="header">
            <div class="header-inner">
                <div class="header-logo">
                    <a href="./">簡易Twitter</a>
                </div>
                <div class="hamburger-menu">
                    <div class="bar"></div>
                    <div class="bar"></div>
                    <div class="bar"></div>
                </div>
                <nav class="nav-menu">
                    <c:if test="${ empty loginUser }">
                        <a href="login">ログイン</a>
                        <a href="signup">登録</a>
                    </c:if>
                    <c:if test="${ not empty loginUser }">
                        <a href="setting">設定</a>
                        <a href="logout">ログアウト</a>
                    </c:if>
                </nav>
            </div>
        </div>
        <c:if test="${ not empty loginUser && not empty showProfile && showProfile }">
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
        <%-- エラーメッセージの表示ロジックもヘッダーに含める（ページ上部に表示されるため） --%>
        <c:if test="${ not empty errorMessages }">
            <div class="errorMessages">
                <ul>
                    <c:forEach items="${errorMessages}" var="errorMessage">
                        <li><c:out value="${errorMessage}" /></li>
                    </c:forEach>
                </ul>
            </div>
            <c:remove var="errorMessages" scope="session" />
        </c:if>

        <%-- <div class="content-wrapper"> または <main> など、ヘッダー直下のコンテンツ開始タグ --%>