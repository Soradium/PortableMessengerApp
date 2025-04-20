package org.soradium.portablemessengerapp.repository;

import org.soradium.portablemessengerapp.entity.Chat;
import org.soradium.portablemessengerapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> getChatByFirstUser(User firstUser);

    Optional<Chat> getChatBySecondUser(User secondUser);

    Optional<List<Chat>> getChatsByFirstUser(User firstUser);

    Optional<List<Chat>> getChatsBySecondUser(User secondUser);

    Optional<Chat> getChatByFirstUserAndSecondUser(User firstUser, User secondUser);

    Optional<Chat> getChatByFirstUserAndSecondUserOrFirstUserAndSecondUser(User firstUser, User secondUser, User firstUser1, User secondUser1);

}
