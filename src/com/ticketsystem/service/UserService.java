package com.ticketsystem.service;

import com.ticketsystem.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class to handle user-related operations.
 */
public class UserService {
    private List<User> users;

    public UserService() {
        this.users = new ArrayList<>();
    }

    public User registerUser(String username, String password) {
        if (getUserByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User(username, password);
        users.add(user);
        return user;
    }

    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username) && user.verifyPassword(password))
                .findFirst();
    }

    public Optional<User> getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    public User getUserById(String userId) {
        return users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public boolean updatePassword(String userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (user != null && user.verifyPassword(oldPassword)) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }
} 