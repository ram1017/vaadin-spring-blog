package com.example.app.controller;

import com.example.app.model.Comment;
import com.example.app.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class CommentWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CommentService commentService;

    @Autowired
    public CommentWebSocketController(SimpMessagingTemplate messagingTemplate, CommentService commentService) {
        this.messagingTemplate = messagingTemplate;
        this.commentService = commentService;
    }

    @MessageMapping("/comment")
    public void handleComment(Comment comment) {
        Comment savedComment = comment;
        messagingTemplate.convertAndSend("/topic/comments/" + savedComment.getPostId(), savedComment);
    }
}
