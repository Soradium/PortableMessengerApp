package org.soradium.portablemessengerapp.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.soradium.portablemessengerapp.entity.Chat;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.repository.ChatRepository;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ChatServiceImplTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatServiceImpl chatService;

    public ChatServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getChatByFirstUser_ReturnsChat() {
        User userA = new User();
        userA.setUsername("userA");
        User userB = new User();
        userB.setUsername("userB");
        Chat chat = new Chat(userA, userB, new ArrayList<>());

        when(chatRepository.getChatByFirstUser(userA)).thenReturn(Optional.of(chat));

        Chat result = chatService.getChatByFirstUser(userA);

        assertEquals(chat, result);
    }

    @Test
    void getChatByFirstUser_ThrowsWhenNotFound() {
        User userA = new User();
        userA.setUsername("userA");

        when(chatRepository.getChatByFirstUser(userA)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> chatService.getChatByFirstUser(userA));
    }

    @Test
    void getOrCreateChat_CreatesWhenNotFound() {
        User userA = new User();
        userA.setUsername("userA");
        User userB = new User();
        userB.setUsername("userB");

        when(chatRepository.getChatByFirstUserAndSecondUserOrFirstUserAndSecondUser(
                userA, userB, userB, userA))
                .thenReturn(Optional.empty());

        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Chat result = chatService.getOrCreateChat(userA, userB);

        assertNotNull(result);
        assertEquals(userA.getUsername().compareTo(userB.getUsername()) < 0 ? userA : userB, result.getFirstUser());
    }
}
