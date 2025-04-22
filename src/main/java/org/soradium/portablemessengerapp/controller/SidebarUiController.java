package org.soradium.portablemessengerapp.controller;

import org.soradium.portablemessengerapp.dto.FriendFetchResponseDto;
import org.soradium.portablemessengerapp.dto.UserRequesterAndUserRequestedToDto;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.service.MessageServiceImpl;
import org.soradium.portablemessengerapp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class SidebarUiController {

    private final MessageServiceImpl messageService;
    private final UserServiceImpl userService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public SidebarUiController(
            MessageServiceImpl messageService,
            UserServiceImpl userService,
            KafkaTemplate<String, Object> kafkaTemplate) {
        this.messageService = messageService;
        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
            id = "get_target",
            topics = "friend-fetch-send",
            containerFactory = "kafkaListenerUserRequestedAndRequestedToContainerFactory"
    )
    public void getTargetToOpenChatWith(
            UserRequesterAndUserRequestedToDto request) {
        String requester = request.requester();
        String requestedTo = request.requestedTo();
        User requesterUser = userService.getUserWithFriendsByUsername(requester);
//        User requestedToUser
//                = userService
//                .getUserByUsername(requestedTo);
        for(User u : requesterUser.getFriends()) {
            if(u.getUsername().equals(requestedTo)) {
                kafkaTemplate.send(
                        "friend-fetch-response",
                        new FriendFetchResponseDto(
                                request.requester(),
                                "Success")
                );
                return;
            }
        }
        kafkaTemplate.send(
                "friend-fetch-response",
                new FriendFetchResponseDto(
                        request.requester(),
                        "No such friend in friend list "
                                + requestedTo)
        );

    }
}

