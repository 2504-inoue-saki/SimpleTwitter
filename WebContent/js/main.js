// main.js

$(function() {

    // ハンバーガーメニューのトグル
    $('.hamburger-menu').on('click', function() {
        // ハンバーガーアイコンとナビゲーションメニューの両方に 'active' クラスをトグル
        $(this).toggleClass('active');
        $('.nav-menu').toggleClass('nav-menu-open');
    });

    // ツイート投稿フォームの非同期化
    // formにid="tweetForm"を付けていることを前提
    $('#tweetForm').on('submit', function(event) {
        event.preventDefault(); // デフォルトのフォーム送信をキャンセル

        const $form = $(this);
        const tweetText = $form.find('textarea[name="text"]').val();

        if (tweetText.length === 0 || tweetText.length > 140) {
            alert('ツイートは1文字以上140文字以下で入力してください。');
            return;
        }

        $.ajax({
            url : $form.attr('action'),
            type : $form.attr('method'),
            data : { text : tweetText },
            dataType : 'json',
            success : function(response) {
                if (response.success) {
                    alert('ツイートが成功しました！');
                    $form.find('textarea[name="text"]').val(''); // フォームをクリア
                    // 投稿後にページをリロードして新しいツイートを表示
                    location.reload();
                } else {
                    alert('ツイートに失敗しました: ' + response.errorMessage);
                }
            },
            error : function(xhr, status, error) {
                console.error('Ajaxエラー (ツイート投稿):', status, error);
                alert('ツイート投稿中に通信エラーが発生しました。');
            }
        });
    });


    // ツイート削除ボタンの非同期処理
    // .delete-formクラス内の.delete-buttonがクリックされたとき
    $(document).on('click', '.delete-form .delete-button', function(event) {
        event.preventDefault(); // デフォルトのフォーム送信をキャンセル

        if (confirm('本当にこのツイートを削除しますか？')) {
            const $form = $(this).closest('form');
            const messageId = $form.find('input[name="message_id"]').val();

            $.ajax({
                url : $form.attr('action'), // 'deleteMessage'
                type : $form.attr('method'), // 'POST'
                data : { message_id : messageId },
                dataType : 'json',
                success : function(response) {
                    if (response.success) {
                        alert('ツイートを削除しました。');
                        // 親の.message-item要素を削除
                        $form.closest('.message-item').remove();
                    } else {
                        alert('削除に失敗しました: ' + response.errorMessage);
                    }
                },
                error : function(xhr, status, error) {
                    console.error('Ajaxエラー (ツイート削除):', status, error);
                    alert('ツイート削除中に通信エラーが発生しました。');
                }
            });
        }
    });

    // コメント削除ボタンの非同期処理
    // .delete-comment-formクラス内の.delete-comment-buttonがクリックされたとき
    $(document).on('click', '.delete-comment-form .delete-comment-button', function(event) {
        event.preventDefault();

        if (confirm('本当にこのコメントを削除しますか？')) {
            const $form = $(this).closest('form');
            const commentId = $form.find('input[name="comment_id"]').val();

            $.ajax({
                url : '/SimpleTwitter/commentdelete',
                type : 'POST',
                data : {
                    comment_id : commentId
                },
                dataType : 'json',
                success : function(response) {
                    if (response.success) {
                        alert('コメントを削除しました。');
                        $form.closest('.comment-item').remove();
                    } else {
                        alert('コメントの削除に失敗しました: ' + response.errorMessage);
                    }
                },
                error : function(xhr, status, error) {
                    console.error('Ajaxエラー (コメント削除):', status, error);
                    alert('コメント削除中に通信エラーが発生しました。');
                }
            });
        }
    });

    // コメント投稿フォームの非同期処理
    // .comment-post-formクラスのフォームが送信されたとき
    $(document).on('submit', '.comment-post-form', function(event) {
        event.preventDefault();

        const $form = $(this);
        const commentText = $form.find('textarea[name="text"]').val();
        const messageId = $form.find('input[name="message_id"]').val();

        if (commentText.length === 0 || commentText.length > 140) {
            alert('コメントは1文字以上140文字以下で入力してください。');
            return;
        }

        $.ajax({
            url : $form.attr('action'),
            type : $form.attr('method'),
            data : {
                message_id : messageId,
                text : commentText
            },
            dataType : 'json',
            success : function(response) {
                if (response.success) {
                    alert('コメントが成功しました！');
                    $form.find('textarea[name="text"]').val(''); // フォームをクリア
                    // コメント投稿後にページをリロードして新しいコメントを表示
                    location.reload();
                } else {
                    alert('コメントに失敗しました: ' + response.errorMessage);
                }
            },
            error : function(xhr, status, error) {
                console.error('Ajaxエラー (コメント投稿):', status, error);
                alert('コメント投稿中に通信エラーが発生しました。');
            }
        });
    });

    // いいね！ボタンのクリックイベント
    $(document).on('click', '.like-button', function(event) {
        event.preventDefault();

        const $button = $(this);
        const messageId = $button.data('message-id');

        const urlBase = '/SimpleTwitter/api/';
        const url = $button.hasClass('liked') ? urlBase + 'unlike' : urlBase + 'like';

        $.ajax({
            url : url,
            type : 'POST',
            data : { message_id : messageId },
            dataType : 'json',
            success : function(response) {
                if (response.success) {
                    const $heartIcon = $button.find('.heart-icon');
                    const $likeCount = $button.find('.like-count');

                    // サーバーからのレスポンスを直接使用
                    // response.new_count と response.liked_by_current_user がJSONに含まれている前提
                    $likeCount.text(response.new_count); // サーバーから返された新しいいいね数に更新

                    if (response.liked_by_current_user) { // サーバーからのいいね状態に基づいてクラスとアイコンを更新
                        $button.addClass('liked');
                        $heartIcon.text('♥'); // ハートを塗りつぶしに
                    } else {
                        $button.removeClass('liked');
                        $heartIcon.text('♡'); // ハートを白抜きに
                    }
                } else {
                    alert('いいね！の処理に失敗しました。' + (response.message || ''));
                }
            },
            error : function(jqXHR, textStatus, errorThrown) {
                console.error('Ajaxエラー (いいね):', textStatus, errorThrown, jqXHR.responseText);

                // HTTPステータスコードが401 (Unauthorized) の場合
                if (jqXHR.status === 401) {
                    const redirectUrl = jqXHR.getResponseHeader('Location'); // Locationヘッダーを取得
                    if (redirectUrl) {
                        alert('ログインが必要です。ログインページへ移動します。');
                        window.location.href = redirectUrl; // 指定されたURLにリダイレクト
                    } else {
                        // Locationヘッダーがない場合のフォールバック
                        try {
                            const errorResponse = JSON.parse(jqXHR.responseText);
                            alert(errorResponse.message + " ログインページへ移動します。");
                            window.location.href = '/SimpleTwitter/login';
                        } catch (e) {
                            console.error("JSONパースエラー:", e);
                            alert("ログインが必要です。ログインページへ移動します。");
                            window.location.href = '/SimpleTwitter/login';
                        }
                    }
                } else {
                    // その他のエラー
                    alert('いいね！処理中に通信エラーが発生しました。再度お試しください。');
                }
            }
        });
    });
});