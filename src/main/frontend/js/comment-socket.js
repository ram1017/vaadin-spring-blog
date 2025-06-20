let stompClient = null;

function connectToComments(postId) {
    const socket = new SockJS('/ws-comments'); // FIXED
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
        stompClient.subscribe('/topic/comments/' + postId, function (message) {
            const comment = JSON.parse(message.body);
            const container = document.querySelector("#comment-container");
            const p = document.createElement("p");
            p.textContent = comment.comment; // FIXED
            container.appendChild(p);
        });
    });
}

function sendComment(commentJson) {
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/comment", {}, JSON.stringify(commentJson)); // FIXED
    }
}
