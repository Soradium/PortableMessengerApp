package org.soradium.portablemessengerapp.repository;

import org.soradium.portablemessengerapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByUsername(String username);

    Optional<User> deleteByUsername(String username);

    Optional<Boolean> searchUserByFriendsContains(User user);

    @Query("select u from users u left join fetch u.friends where u.username = :username")
    Optional<User> userFetchFriends(@Param("username") String username);


}
