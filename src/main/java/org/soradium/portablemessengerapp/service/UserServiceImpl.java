package org.soradium.portablemessengerapp.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl {
//    RingBufferListWithLimitedSize<User> buffer
//            = new RingBufferListWithLimitedSize<>(100);

    private UserRepository repository;
    private EntityManager em;

    public UserServiceImpl() {

    }

    public UserServiceImpl(
            UserRepository repository) {
        this.repository = repository;
    }

    public User createUser(User user) {
        return repository.save(user);
    }

    public User updateUser(User user) {
        return repository.save(user);
    }

    public List<User> findAllUsers() {
        return repository.findAll();
    }

    public UserRepository getRepository() {
        return repository;
    }

    @Autowired
    public void setRepository(
            UserRepository repository) throws Exception {
        if (repository != null) {
            this.repository = repository;
        } else {
            throw new Exception(
                    "No repository " +
                            "bean found!");
        }
    }

    public User getUserByUsername(String username) {
        return repository
                .getUserByUsername(username)
                .orElse(null);
    }

    public User deleteUserByUsername(String username) {
        return repository
                .deleteByUsername(username)
                .orElse(null);
    }

    public User getUserWithFriendsByUsername(String username) {
        User u = getUserByUsername(username);
        if (u != null) {
            u.getFriends().size();
        }
        return u;
    }

//    public User getUserWithMessagesByUsername(String username) {
//        User u = getUserByUsername(username);
//        if (u != null) {
//            u.getMessagesSent().size();
//            u.getMessagesReceived().size();
//        }
//        return u;
//    }

//    public User getUserWithMessagesAndFriendsByUsername(String username) {
//        User u = getUserByUsername(username);
//        if (u != null) {
//            u.getMessagesSent().size();
//            u.getMessagesReceived().size();
//            u.getFriends().size();
//        }
//        return u;
//    }

    @Autowired
    public void setEntityManager(
            EntityManager em) throws Exception {
        if (em != null) {
            this.em = em;
        } else {
            throw new Exception(
                    "No entity manager " +
                            "bean found!");
        }
    }

    public boolean checkFriendship(User fU, User sU) {
        return fU.getFriends().contains(sU);
    }


}
