package org.soradium.portablemessengerapp.controller;

import org.soradium.portablemessengerapp.dto.MessageDto;
import org.soradium.portablemessengerapp.entity.Chat;
import org.soradium.portablemessengerapp.entity.Message;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.service.ChatServiceImpl;
import org.soradium.portablemessengerapp.service.MessageServiceImpl;
import org.soradium.portablemessengerapp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final SimpMessagingTemplate simpMessagingTemplate;
    //    @MessageMapping("/map")
//    @SendTo("/topics/messages-topic")
//    public String demoResponse(
//            demoMessage demoMessage) {
//
//        return "Hello world! "+demoMessage+" ";
//    }
//
//    public record demoMessage(String message) implements Serializable {}
    private final MessageServiceImpl messageService;
    private final UserServiceImpl userService;
    private final ChatServiceImpl chatService;

    @Autowired
    public ChatController(
            SimpMessagingTemplate messagingTemplate,
            ChatServiceImpl chatService,
            UserServiceImpl userService,
            MessageServiceImpl messageService) {
        this.simpMessagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
        this.messageService = messageService;
    }

    @MessageMapping("/map")
    @SendToUser("/topics/messages-topic")
    public ChatStatusResponse routeMessage(
            SentMessage sentMessage, Principal sender) {
        User senderUser = userService.getUserWithFriendsByUsername(sender.getName());
        User receiverUser = senderUser.getFriends()
                .stream().filter(u -> u.getUsername()
                        .equals(sentMessage.targetUserName()))
                .toList().get(0);

        if (receiverUser == null) {
            return new ChatStatusResponse(
                    "403",
                    "Can't send a message. " + receiverUser
                            + " is not a friend of " + senderUser);
        }
        // all good

        Chat chat = null;
        try {
            chat = chatService.getChatByTwoUsersWithMessages(receiverUser, senderUser);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ChatStatusResponse(
                    "403",
                    "Can't send a message. Chat is inaccessible.");
        }
        chat.getMessages().add(messageService.createMessage(new Message(
                sentMessage.message,
                chat,
                senderUser))
        );
        chat = chatService.saveChat(chat);
        this.simpMessagingTemplate.convertAndSendToUser(
                sentMessage.targetUserName,
                "/topics/messages-topic",
                sentMessage.message);

        return new ChatStatusResponse("200", sentMessage.message);
        //This entire block can't create chats anymore.
    }

    @PostMapping("/retrieve-messages-per-user")
    public ResponseEntity<List<MessageDto>> showAllMessages(
            @RequestBody GetChatRequestPerUser userRequest, Principal sender) {
        String requestedToUsername = userRequest.requestedToUser();
        User requesting = userService.getUserWithFriendsByUsername(sender.getName());
        User requestedTo = requesting.getFriends()
                .stream().filter(u -> u.getUsername()
                        .equals(userRequest.requestedToUser()))
                .toList().get(0);

        if (requestedTo == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        // all good

        Chat chat = chatService.getChatByTwoUsersWithMessages(requesting, requestedTo);
        if (chat == null) {
            chat = new Chat(requesting, requestedTo, new ArrayList<>());
            chatService.saveChat(chat);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.CREATED);
        }
        List<MessageDto> messages = new ArrayList<>(chat.getMessages().size());
        for (Message m : chat.getMessages()) {
            messages.add(new MessageDto(m.getMessageTextData(), m.getSender().getUsername()));
        }
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    public record SentMessage(String targetUserName, String message) {
    }

    public record ChatStatusResponse(String status, String message) {
    }

    public record GetChatRequestPerUser(String requestedToUser) {
    }

}
