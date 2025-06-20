package com.example.app.service;

import com.example.app.Repository.CommentRepository;
import com.example.app.model.Comment;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getByPostId(Integer postId) {
        return commentRepository.findByPostId(postId);
    }

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }
}
