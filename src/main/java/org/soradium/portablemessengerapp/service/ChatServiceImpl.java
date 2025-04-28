package org.soradium.portablemessengerapp.service;

import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.soradium.portablemessengerapp.entity.Chat;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.repository.ChatRepository;
import org.soradium.portablemessengerapp.tools.BufferList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final ChatRepository repository;
    private final BufferList<Chat> bufferList;

    @Autowired
    public ChatServiceImpl(
            ChatRepository repository,
            @Qualifier("chatRingBuffer") BufferList<Chat> bufferList) {
        this.repository = repository;
        this.bufferList = bufferList;
    }

    @Override
    public Chat getChatByFirstUser(User firstUser) {
        // Try getting from buffer first
        for (Chat chat : bufferList.getList()) {
            if (chat.getFirstUser().equals(firstUser)) {
                return chat;
            }
        }
        // Else from DB
        Chat chat = repository.getChatByFirstUser(firstUser).orElse(null);
        if (chat != null) {
            bufferList.addToList(String.valueOf(chat.getId()), chat);
        }
        return chat;
    }

    @Override
    public Chat getChatBySecondUser(User secondUser) {
        for (Chat chat : bufferList.getList()) {
            if (chat.getSecondUser().equals(secondUser)) {
                return chat;
            }
        }
        Chat chat = repository.getChatBySecondUser(secondUser).orElse(null);
        if (chat != null) {
            bufferList.addToList(String.valueOf(chat.getId()), chat);
        }
        return chat;
    }

    @Override
    public Chat getChatByTwoUsers(User firstUser, User secondUser) {
        for (Chat chat : bufferList.getList()) {
            if ((chat.getFirstUser()
                    .getUsername()
                    .equals(firstUser.getUsername())
                    && chat.getSecondUser()
                    .getUsername()
                    .equals(secondUser.getUsername()))
                    || (chat.getFirstUser()
                            .getUsername()
                            .equals(secondUser.getUsername())
                            && chat.getSecondUser()
                            .getUsername()
                            .equals(firstUser.getUsername()))) {
                return chat;
            }
        }
        Chat chat = repository.getChatByFirstUserAndSecondUserOrFirstUserAndSecondUser(firstUser, secondUser, secondUser, firstUser)
                .orElse(null);
        if (chat != null) {
            bufferList.addToList(String.valueOf(chat.getId()), chat);
        }
        return chat;
    }

    @Override
    public Chat getOrCreateChat(User u1, User u2) {
        Chat chat = getChatByTwoUsers(u1, u2);
        if (chat == null) {
            chat = new Chat();
            chat.setFirstUser(u1);
            chat.setSecondUser(u2);
            chat = repository.save(chat);
            bufferList.addToList(String.valueOf(chat.getId()), chat);
        }
        return chat;
    }

    @Override
    public Chat getChatByTwoUsersWithMessages(User firstUser, User secondUser) {
        Chat chat = getChatByTwoUsers(firstUser, secondUser);
        if (chat != null && !Hibernate.isInitialized(chat.getMessages())) {
            Hibernate.initialize(chat.getMessages());
            chat = repository
                    .getChatByTwoUsersWithMessages(firstUser.getUsername(), secondUser.getUsername())
                    .orElseThrow();
            bufferList.setByString(String.valueOf(chat.getId()), chat);
        }
        return chat;
    }

    @Override
    public Chat saveChat(Chat chat) {
        bufferList.setByString(String.valueOf(chat.getId()), chat);
        return repository.save(chat);
    }

    @Override
    public void deleteChat(Chat chat) {
        bufferList.setByString(String.valueOf(chat.getId()), null);
        repository.delete(chat);
    }

    @Override
    public Chat getChatById(long id) {
        Chat chat = bufferList.getByString(String.valueOf(id));
        if (chat != null) {
            return chat;
        }
        chat = repository.findById(id).orElse(null);
        if (chat != null) {
            Chat cachedChat = bufferList.addToList(String.valueOf(chat.getId()), chat);
            if (cachedChat != null) {
                repository.save(cachedChat);
            }
        }

        return chat;
    }

    @PreDestroy
    public void saveAllChats() {
        repository.saveAll(bufferList.getList());
    }
}
