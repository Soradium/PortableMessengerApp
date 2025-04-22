package org.soradium.portablemessengerapp.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.soradium.portablemessengerapp.entity.Message;
import org.soradium.portablemessengerapp.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MessageServiceImpl {
    private MessageRepository repository;
    private EntityManager em;

    public MessageServiceImpl() {
    }

    public MessageServiceImpl(
            MessageRepository repository) {
        this.repository = repository;
    }

    public Message createMessage(Message message) {
        return repository.save(message);
    }

    public Message updateMessage(Message message) {
        return repository.save(message);
    }

    public void deleteMessageById(Long messageId) {
        repository.deleteById(messageId);
    }

    public Message findMessageById(Long messageId) {
        return repository.findById(messageId).orElse(null);
    }

    public List<Message> findAllMessages() {
        return repository.findAll();
    }

    public MessageRepository getRepository() {
        return repository;
    }

    @Autowired
    public void setRepository(
            MessageRepository repository) throws Exception {
        if (repository != null) {
            this.repository = repository;
        } else {
            throw new Exception(
                    "No repository " +
                            "bean found!");
        }
    }

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
}
