<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%-- ページタイトルを設定 --%>
<% request.setAttribute("pageTitle", "タイムライン"); %>

<jsp:include page="/WEB-INF/views/layout/header.jsp" />

<div class="filter-area">
	<h3>つぶやき絞り込み</h3>
	<form action="./" method="get">
		期間： <input name="start" type="date" value="${start}" /> ～ <input
			name="end" type="date" value="${end}" /> <input type="submit"
			value="絞り込み" />
	</form>
</div>

<div class="form-area">
	<c:if test="${ isShowMessageForm }">
		<form action="message" method="post" id="tweetForm">
			<textarea name="text" class="tweet-box" placeholder="今どうしてる？"
				maxlength="140"></textarea>
			<input type="submit" value="つぶやく">
		</form>
	</c:if>
</div>

<div class="messages-area">
	<c:choose>
		<c:when test="${messages != null && !messages.isEmpty()}">
			<c:forEach items="${messages}" var="message">
				<div class="message-item">
					<div class="message">
						<div class="account-name">
							<a class="account"
								href="./?user_id=<c:out value="${message.userId}"/>">@<c:out
									value="${message.account}" /></a> <span class="name"><c:out
									value="${message.name}" /></span>
						</div>
						<div class="text">
							<c:out value="${message.text}" />
						</div>
						<div class="date-actions">
							<span class="date"> <fmt:formatDate
									value="${message.createdDate}" pattern="yyyy/MM/dd HH:mm:ss" />
								<c:if
									test="${message.updatedDate != null && message.createdDate.time != message.updatedDate.time}">
                                            (更新: <fmt:formatDate
										value="${message.updatedDate}" pattern="yyyy/MM/dd HH:mm:ss" />)
                                        </c:if>
							</span>

							<div class="actions">
								<button
									class="like-button ${message.likedByCurrentUser ? 'liked' : ''}"
									data-message-id="${message.id}">
									<span class="heart-icon">${message.likedByCurrentUser ? '♥' : '♡'}</span>
									<span class="like-count">${message.likeCount}</span>
								</button>

								<c:if test="${loginUser.id == message.userId}">
									<form action="deleteMessage" method="post" class="delete-form">
										<input type="hidden" name="message_id" value="${message.id}">
										<button type="submit" class="delete-button">削除</button>
									</form>
								</c:if>
								<c:if test="${message.userId == loginUser.id}">
									<form action="edit" method="get" class="edit-form">
										<input name="message_id" type="hidden" value="${message.id}">
										<button type="submit" class="edit-button">編集</button>
									</form>
								</c:if>
							</div>
						</div>
					</div>
					<%-- .message の閉じタグ --%>
					<div class="comments-section">
						<div class="comments-list">
							<c:forEach items="${comments}" var="comment">
								<c:if test="${message.id == comment.messageId}">
									<div class="comment-item">
										<div class="comment-account-name">
											<a class="account" href="./?user_id=${comment.userId}">@<c:out
													value="${comment.account}" /></a> <span class="name"><c:out
													value="${comment.name}" /></span>
										</div>
										<div class="comment-text">
											<c:out value="${comment.text}" />
										</div>
										<div class="comment-date-actions">
											<span class="comment-date"> <fmt:formatDate
													value="${comment.createdDate}"
													pattern="yyyy/MM/dd HH:mm:ss" />
											</span>
											<c:if test="${loginUser.id == comment.userId}">
												<form action="deleteComment" method="post"
													class="delete-comment-form">
													<input type="hidden" name="comment_id"
														value="${comment.id}"> <input type="hidden"
														name="message_id" value="${message.id}">
													<button type="submit" class="delete-comment-button">削除</button>
												</form>
											</c:if>
										</div>
									</div>
								</c:if>
							</c:forEach>
						</div>

						<div class="reply-form-area">
							<c:if test="${ isShowMessageForm }">
								<form action="comment" method="post" class="comment-post-form">
									<input name="message_id" type="hidden" value="${message.id}">
									<textarea name="text" class="reply-box" placeholder="返信を書く"
										maxlength="140"></textarea>
									<input type="submit" value="返信">
								</form>
							</c:if>
						</div>
					</div>
				</div>
				<%-- .message-item の閉じタグ --%>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<p class="no-messages">まだつぶやきがありません。</p>
		</c:otherwise>
	</c:choose>
</div>
<%-- <div class="copyright">Copyright &copy; Saki Inoue</div> は footer.jsp に移動 --%>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />