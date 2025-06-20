package com.example.app.service;


import com.example.app.Repository.PostRepository;
import com.example.app.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PostService {
    public final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getAllPosts() {

        return postRepository.findAll();
    }
    public void save(Post post) {

        postRepository.save(post);
    }

    public Post getById(Integer postId) {

        return postRepository.findById(postId).get();
    }

    public List<Post> getPostById(int userId) {
        return postRepository.findByUserId(userId);
    }


}
