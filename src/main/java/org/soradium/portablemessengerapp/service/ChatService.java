package org.soradium.portablemessengerapp.service;

import org.soradium.portablemessengerapp.entity.Chat;
import org.soradium.portablemessengerapp.entity.User;

public interface ChatService {
    Chat getChatByFirstUser(User firstUser);

    Chat getChatBySecondUser(User secondUser);

    Chat getChatByTwoUsers(User firstUser, User secondUser);

    Chat getOrCreateChat(User u1, User u2);

    Chat getChatByTwoUsersWithMessages(User firstUser, User secondUser);

    Chat saveChat(Chat chat);

    void deleteChat(Chat chat);

    Chat getChatById(long id);
    
}
