package org.soradium.portablemessengerapp.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

public class ChatControllerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private MessageServiceImpl messageService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ChatServiceImpl chatService;

    @InjectMocks
    private ChatController chatController;

    public ChatControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendMessage_SenderDoesNotExist() {
        MessageDto message = new MessageDto("Hello!", "nonexistentSender", "receiver");

        when(userService.getUserWithFriendsByUsername("nonexistentSender")).thenReturn(null);

        chatController.sendMessage(message);

        verify(kafkaTemplate, times(1)).send(
                eq("chat-response"),
                argThat(response -> response.toString().contains("Sender 'nonexistentSender' does not exist"))
        );
    }

    @Test
    public void testSendMessage_NotFriendsWithReceiver() {
        MessageDto message = new MessageDto("Hello!", "sender", "receiver");
        User senderUser = new User();
        User anotherFriend = new User();
        anotherFriend.setUsername("someName");
        senderUser.setFriends(List.of(anotherFriend));
        senderUser.setUsername("sender");

        when(userService.getUserWithFriendsByUsername("sender")).thenReturn(senderUser);

        chatController.sendMessage(message);

        verify(kafkaTemplate, times(1)).send(
                eq("chat-response"),
                argThat(response -> response.toString().contains("is not a friend of sender"))
        );
    }

    @Test
    public void testSendMessage_ChatInaccessible() {
        MessageDto message = new MessageDto("Hello!", "sender", "receiver");

        User receiverUser = new User();
        receiverUser.setUsername("receiver");

        User senderUser = new User();
        senderUser.setFriends(List.of(receiverUser));

        when(userService.getUserWithFriendsByUsername("sender")).thenReturn(senderUser);
        doThrow(new RuntimeException("Chat inaccessible")).when(chatService).getChatByTwoUsersWithMessages(receiverUser, senderUser);

        chatController.sendMessage(message);

        verify(kafkaTemplate, times(1)).send(
                eq("chat-response"),
                argThat(response -> response.toString().contains("Chat is inaccessible"))
        );
    }

    @Test
    public void testSendMessage_Successful() {
        MessageDto message = new MessageDto("sender", "receiver", "Hello!");
        Chat chat = new Chat();

        User receiverUser = new User();
        receiverUser.setUsername("receiver");

        User senderUser = new User();
        senderUser.setFriends(List.of(receiverUser));

        when(userService.getUserWithFriendsByUsername("sender")).thenReturn(senderUser);
        when(chatService.getChatByTwoUsersWithMessages(receiverUser, senderUser)).thenReturn(chat);
        when(messageService.createMessage(any(Message.class))).thenReturn(new Message("Hello!", chat, senderUser));

        chatController.sendMessage(message);

        verify(kafkaTemplate, times(1)).send(
                eq("chat-response"),
                argThat(response -> response.toString().contains("Hello!"))
        );
    }
}
