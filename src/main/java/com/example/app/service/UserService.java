package com.example.app.service;

import com.example.app.Repository.UserRepository;
import com.example.app.model.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String getUsernameById(Integer id) {
        return userRepository.findById(id).map(User::getUsername).orElse("Unknown User");
    }

    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    public User save(User user) {

        return userRepository.save(user);
    }
}
