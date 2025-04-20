package org.soradium.portablemessengerapp.repository;

import org.soradium.portablemessengerapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByUsername(String username);

    Optional<User> deleteByUsername(String username);

    Optional<Boolean> searchUserByFriendsContains(User user);
}
