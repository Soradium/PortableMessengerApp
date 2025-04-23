package org.soradium.portablemessengerapp.service;

import org.soradium.portablemessengerapp.entity.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    List<User> findAllUsers();

    User getUserByUsername(String username);

    User deleteUserByUsername(String username);

    User getUserWithFriendsByUsername(String username);

    boolean checkFriendship(User fU, User sU);
}

