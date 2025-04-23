package org.soradium.portablemessengerapp.service;

import jakarta.transaction.Transactional;
import org.soradium.portablemessengerapp.entity.Chat;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
public class ChatServiceImpl implements ChatService{
    private final ChatRepository repository;

    public ChatServiceImpl(@Autowired ChatRepository repository) {
        this.repository = repository;
    }

    public Chat getChatByFirstUser(User firstUser) {
        return repository.getChatByFirstUser(firstUser).orElseThrow();
    }

    public Chat getChatBySecondUser(User secondUser) {
        return repository.getChatBySecondUser(secondUser).orElseThrow();
    }

    public Chat getChatByTwoUsers(User firstUser, User secondUser) {
        return repository
                .getChatByFirstUserAndSecondUserOrFirstUserAndSecondUser(
                        firstUser, secondUser, secondUser, firstUser)
                .orElseThrow();
    }

    public Chat getOrCreateChat(User u1, User u2) {
        return repository
                .getChatByFirstUserAndSecondUserOrFirstUserAndSecondUser(u1, u2, u2, u1)
                .orElseGet(() -> {
                    User first = u1.getUsername().compareTo(u2.getUsername()) < 0 ? u1 : u2;
                    User second = (first == u1) ? u2 : u1;
                    return repository.save(new Chat(first, second, new ArrayList<>()));
                });
    }


    public Chat getChatByTwoUsersWithMessages(User firstUser, User secondUser) {
        Chat chat;
        try {
            chat = repository
                    .getChatByFirstUserAndSecondUserOrFirstUserAndSecondUser(
                            firstUser, secondUser, secondUser, firstUser)
                    .orElseThrow();
        } catch (Exception e) {
            System.out.println("------" + e.getMessage());
            return null;
        }
        chat.getMessages().size();
        return chat;
    }

    public Chat saveChat(Chat chat) {
        Optional<Chat> existingChat = repository.getChatByFirstUserAndSecondUserOrFirstUserAndSecondUser(
                chat.getFirstUser(), chat.getSecondUser(), chat.getSecondUser(), chat.getFirstUser());
        return existingChat.orElseGet(() -> repository.save(chat));
    }

    public void deleteChat(Chat chat) {
        repository.delete(chat);
    }

    public Chat getChatById(long id) {
        return repository.findById(id).orElseThrow();
    }

    // Add other service methods as needed
}
