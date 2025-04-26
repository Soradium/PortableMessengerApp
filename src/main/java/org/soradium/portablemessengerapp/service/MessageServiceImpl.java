package org.soradium.portablemessengerapp.service;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.soradium.portablemessengerapp.entity.Message;
import org.soradium.portablemessengerapp.repository.MessageRepository;
import org.soradium.portablemessengerapp.tools.BufferList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final BufferList<Message> bufferList;
    private final MessageRepository repository;

    @Autowired
    public MessageServiceImpl(
            MessageRepository repository,
            @Qualifier("messageRingBuffer") BufferList<Message> bufferList) {
        this.repository = repository;
        this.bufferList = bufferList;
    }

    @Override
    public Message createMessage(Message message) {
        Message overwritten = bufferList.addToList(String.valueOf(message.getMessageId()), message);
        if (overwritten != null) {
            repository.save(overwritten);
        }
        return message;
    }

    @Override
    public Message updateMessage(Message message) {
        String messageIdStr = String.valueOf(message.getMessageId());
        Message existing = bufferList.getByString(messageIdStr);
        if (existing == null) {
            bufferList.addToList(messageIdStr, message);
            return repository.save(message);
        } else {
            return bufferList.setByString(messageIdStr, message);
        }
    }

    @Override
    public void deleteMessageById(Long messageId) {
        bufferList.setByString(String.valueOf(messageId), null);
        repository.deleteById(messageId);
    }

    @Override
    public Message findMessageById(Long messageId) {
        try {
            String messageIdStr = String.valueOf(messageId);
            Message message = bufferList.getByString(messageIdStr);
            if (message != null) {
                return message;
            }
            message = repository.findById(messageId).orElse(null);
            if (message != null) {
                Message cachedOut = bufferList.addToList(messageIdStr, message);
                if (cachedOut != null) {
                    repository.save(cachedOut);
                }
            }
            return message;
        } catch (Exception e) {
            log.error("Couldn't find message: {}, exception: {}", messageId, e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Message> findAllMessages() {
        return repository.findAll();
    }

    @PreDestroy
    public void saveAllMessages() {
        repository.saveAll(bufferList.getList());
    }
}
