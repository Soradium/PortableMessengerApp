package org.soradium.portablemessengerapp.controller;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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

        log.info("Received chat open request: '{}' wants to chat with '{}'", requester, requestedTo);
        try {
            User requesterUser = userService.getUserWithFriendsByUsername(requester);
            for (User u : requesterUser.getFriends()) {
                if (u.getUsername().equals(requestedTo)) {
                    log.info("Chat open request successful: '{}' is a friend of '{}'", requestedTo, requester);
                    kafkaTemplate.send(
                            "friend-fetch-response",
                            new FriendFetchResponseDto(
                                    request.requester(),
                                    "Success")
                    );
                    return;
                }
            }

            log.warn("Chat open request failed: '{}' is not in '{}' friend list", requestedTo, requester);
            kafkaTemplate.send(
                    "friend-fetch-response",
                    new FriendFetchResponseDto(
                            request.requester(),
                            "No such friend in friend list "
                                    + requestedTo)
            );
        } catch (Exception e) {
            log.error("Could not get chat target, " +
                    "requester: {}, requested to: {}," +
                    " exception: {}",
                    requester,
                    requestedTo, e.getMessage());
            throw e;
        }
    }
}

