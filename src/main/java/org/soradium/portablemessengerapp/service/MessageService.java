package org.soradium.portablemessengerapp.service;

import org.soradium.portablemessengerapp.entity.Message;
import org.soradium.portablemessengerapp.repository.MessageRepository;

import java.util.List;

public interface MessageService {
    Message createMessage(Message message);

    Message updateMessage(Message message);

    void deleteMessageById(Long messageId);

    Message findMessageById(Long messageId);

    List<Message> findAllMessages();

    MessageRepository getRepository();
}

