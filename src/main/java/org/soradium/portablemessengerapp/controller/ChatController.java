package org.soradium.portablemessengerapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.soradium.portablemessengerapp.dto.MessageDto;
import org.soradium.portablemessengerapp.dto.RequestMessageListDto;
import org.soradium.portablemessengerapp.dto.SenderAndRetrievedMessageListDto;
import org.soradium.portablemessengerapp.dto.SentMessageDto;
import org.soradium.portablemessengerapp.entity.Chat;
import org.soradium.portablemessengerapp.entity.Message;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.service.ChatServiceImpl;
import org.soradium.portablemessengerapp.service.MessageServiceImpl;
import org.soradium.portablemessengerapp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class ChatController {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MessageServiceImpl messageService;
    private final UserServiceImpl userService;
    private final ChatServiceImpl chatService;

    @Autowired
    public ChatController(
            ChatServiceImpl chatService,
            UserServiceImpl userService,
            MessageServiceImpl messageService,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.chatService = chatService;
        this.userService = userService;
        this.messageService = messageService;
    }

    @KafkaListener(
            topics = "chat-send",
            id = "send_message",
            containerFactory = "kafkaListenerMessageDtoContainerFactory"
    )
    public void sendMessage(MessageDto message) {
        String sender = message.sender();
        String receiver = message.receiver();

        log.info("Received message send request from '{}' to '{}'"
                , sender, receiver);
        try {
            User senderUser = userService.getUserWithFriendsByUsername(sender);
            User receiverUser = senderUser.getFriends()
                    .stream().filter(u -> u.getUsername()
                            .equals(receiver))
                    .toList().get(0);

            if (receiverUser == null) {
                String warnMsg = "Can't send message to user '" + receiver + "' - not a friend of '" + sender + "'";
                log.warn(warnMsg);
                kafkaTemplate.send(
                        "chat-response",
                        new SentMessageDto(sender,
                                "Can't send message to user " + receiver
                                        + " - is not a friend of " + sender)
                );
                return;
            }

            Chat chat = null;
            try {
                chat = chatService.getChatByTwoUsersWithMessages(receiverUser, senderUser);
                log.debug("Fetched chat between '{}' and '{}'", sender, receiver);
            } catch (Exception e) {
                log.error("Error fetching chat: {}", e.getMessage(), e);
                kafkaTemplate.send(
                        "chat-response",
                        new SentMessageDto(sender, "Can't send a message. Chat is inaccessible.")
                );
                return;
            }
            chat.getMessages().add(messageService.createMessage(new Message(
                    message.message(),
                    chat,
                    senderUser))
            );
            chat = chatService.saveChat(chat);

            log.info("Message from '{}' to '{}' saved and chat updated", sender, receiver);

            kafkaTemplate.send(
                    "chat-response",
                    new SentMessageDto(receiver, message.message())
            );
        } catch (Exception e) {
            log.error("Unexpected error while processing message from '{}' to '{}': {}",
                    message.sender(), message.receiver(), e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
            id = "retrieve_all_messages",
            topics = "message-send",
            containerFactory = "kafkaListenerRequestMessageListContainerFactory"
    )
    public void retrieveAllMessagesPerChat(RequestMessageListDto request) {
        String requesterUsername = request.requester();
        String requestedToUsername = request.requestedTo();

        log.info("Retrieve messages request from '{}' to '{}'", requesterUsername, requestedToUsername);

        try {
            User requesting = userService.getUserWithFriendsByUsername(requesterUsername);
            User requestedTo = requesting.getFriends()
                    .stream().filter(u -> u.getUsername()
                            .equals(requestedToUsername))
                    .toList().get(0);
            if (requestedTo == null) {
                log.warn("User '{}' is not a friend of '{}'. Returning NULLCHAT.", requestedToUsername, requesterUsername);
                kafkaTemplate.send(
                        "message-response",
                        new SenderAndRetrievedMessageListDto(requesterUsername, new ArrayList<>(), "NULLCHAT")
                );
                System.out.println("NULL RESPONSE! NO SUCH USER TO WHICH REQUEST WAS MADE");
                return;
                // convert to exception, process logic into
            }

            Chat chat = chatService.getChatByTwoUsersWithMessages(requesting, requestedTo);
            if (chat == null) {
                log.info("No existing chat found between '{}' and '{}'. Creating new chat.", requesterUsername, requestedToUsername);
                chat = new Chat(requesting, requestedTo, new ArrayList<>());
                chatService.saveChat(chat);
                kafkaTemplate.send(
                        "message-response",
                        new SenderAndRetrievedMessageListDto(requesterUsername, new ArrayList<>(), "NEWCHAT")
                );
                return;
            }
            List<SentMessageDto> messages = new ArrayList<>(chat.getMessages().size());
            for (Message m : chat.getMessages()) {
                messages.add(new SentMessageDto(m.getSender().getUsername(), m.getMessageTextData()));
            }

            log.debug("Returning {} messages for chat between '{}' and '{}'", messages.size(), requesterUsername, requestedToUsername);

            kafkaTemplate.send(
                    "message-response",
                    new SenderAndRetrievedMessageListDto(requesterUsername, messages, "OKCHAT")
            );
        } catch (Exception e) {
            log.error("Failed to retrieve messages between '{}' and '{}'. Error: {}",
                    requesterUsername, requestedToUsername, e.getMessage(), e);
            throw e;
        }
    }
}
