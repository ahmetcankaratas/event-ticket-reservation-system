package com.ticketsystem.service;

import com.ticketsystem.model.User;
import com.ticketsystem.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class to handle user-related operations.
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String password, String role) {
        if (getUserByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User(username, password, role);
        userRepository.save(user);
        return user;
    }

    public Optional<User> login(String username, String password) {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent() && optionalUser.get().verifyPassword(password)) {
            return optionalUser;
        }

        else {
            return Optional.empty();
        }
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean updatePassword(UUID userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (user != null && user.verifyPassword(oldPassword)) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }
} 