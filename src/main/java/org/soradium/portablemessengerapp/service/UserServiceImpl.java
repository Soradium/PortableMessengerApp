package org.soradium.portablemessengerapp.service;

import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.repository.UserRepository;
import org.soradium.portablemessengerapp.tools.BufferList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    private final BufferList<User> bufferList;
    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(
            UserRepository repository,
            @Qualifier("userRingBuffer") BufferList<User> bufferList) {
        this.repository = repository;
        this.bufferList = bufferList;
    }

    // Don't forget the predestroy!
    public User createUser(User user) {
        User userOverwritten = bufferList.addToList(user.getUsername(), user);
        if (userOverwritten != null) {
            repository.save(userOverwritten);
        }
        return user;
    }

    public User getUserByUsername(String username) {
        try {
            User u = bufferList.getByString(username);
            if (u != null) {
                return u;
            }
            u = repository
                    .getUserByUsername(username)
                    .orElse(null);
            User cachedOut = bufferList.addToList(username, u);
            if (cachedOut != null) {
                repository.save(cachedOut);
            }
            return u;
        } catch (Exception e) {
            log.error("Couldn't get user: {}, exception: {}",
                    username, e.getMessage());
            throw e;
        }
    }

    public User getUserWithFriendsByUsername(String username) {
        User u = getUserByUsername(username);
        try {
            if (u != null && !Hibernate.isInitialized(u.getFriends())) {
                u = repository.userFetchFriends(username)
                        .orElseThrow();
                bufferList.setByString(username, u);
            }
        } catch (Exception e) {
            log.error("Error in fetching user: {}, exception: {}", username, e.getMessage());
            throw e;
        }
        return u;
    }

    public User updateUser(User user) {
        String username = user.getUsername();
        User u = bufferList.getByString(username);
        if (u == null) {
            bufferList.addToList(username, user);
            return repository.save(user);
        } else {
            return bufferList.setByString(username, user);
        }
    }

    public List<User> findAllUsers() {
        return repository.findAll();
    }

    public User deleteUserByUsername(String username) {
        bufferList.setByString(username, null);

        return repository
                .deleteByUsername(username)
                .orElse(null);
    }

    public boolean checkFriendship(User fU, User sU) {
        return fU.getFriends().contains(sU);
    }

    @PreDestroy
    public void saveAllUsers() {
        repository.saveAll(bufferList.getList());
    }
}
