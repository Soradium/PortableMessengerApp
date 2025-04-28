package org.soradium.portablemessengerapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.soradium.portablemessengerapp.dto.FriendRequestResponseDto;
import org.soradium.portablemessengerapp.dto.FriendRequestSenderAndReceiverDto;
import org.soradium.portablemessengerapp.dto.UsernameAsObjectDto;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.soradium.portablemessengerapp.dto.FriendsListFetchResponseDto;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
public class FriendsController {

    private final UserService userService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    // move into thread pools, work with requests as futures

    @Autowired
    public FriendsController(
            UserService userService,
            KafkaTemplate<String, Object> kafkaMessageTemplate) {
        this.userService = userService;
        this.kafkaTemplate = kafkaMessageTemplate;
    }

    @KafkaListener(
            id = "fetch_friend_list_request",
            topics = "friendlist-fetch-send",
            containerFactory = "kafkaListenerUsernameAsDtoContainerFactory"
    )
    public void fetchFriends(UsernameAsObjectDto object) {
        String requesterUsername = object.username();
        User u = userService.getUserWithFriendsByUsername(requesterUsername);
        List<String> friends = u.getFriends().stream()
                .map(User::getUsername).toList();
        kafkaTemplate.send(
                "friendlist-fetch-response",
                new FriendsListFetchResponseDto(requesterUsername, friends)
                );

    }

    @KafkaListener(
            id = "add_friend",
            topics = "friend-add",
            containerFactory = "kafkaListenerFriendRequestContainerFactory"
    )
    public void addFriend(FriendRequestSenderAndReceiverDto request) {
        String senderUsername = request.senderUsername();
        String receiverUsername = request.receiverUsername();

        log.info("Received friend request from '{}' to '{}'", senderUsername, receiverUsername);
        try {
            User friendUser =
                    userService.getUserByUsername(receiverUsername);
            if (friendUser == null) {
                log.warn("Friend request failed: User '{}' does not exist", receiverUsername);
                kafkaTemplate.send(
                        "friend-response",
                        new FriendRequestResponseDto(senderUsername,
                                "User " + receiverUsername
                                        + " does not exist")
                );
                return;
            }
            User currentUser =
                    userService.getUserWithFriendsByUsername(senderUsername);
            if (currentUser.getFriends().contains(friendUser)) {
                log.info("Friend request ignored: '{}' already has '{}' as friend", senderUsername, receiverUsername);
                kafkaTemplate.send(
                        "friend-response",
                        new FriendRequestResponseDto(senderUsername,
                                "You already have friend "
                                        + receiverUsername)
                );
                return;
            }
            currentUser.getFriends().add(friendUser);
            userService.updateUser(currentUser);
            // will this also add at the other side? no, will not, will have to
            // send request from another side (consider for future update)
            log.info("Friend request from '{}' to '{}' processed successfully", senderUsername, receiverUsername);

            kafkaTemplate.send(
                    "friend-response",
                    new FriendRequestResponseDto(senderUsername,
                            "Successfully requested friend "
                                    + receiverUsername)
            );
        } catch (Exception e) {
            log.error("Unexpected error during friend request from '{}' to '{}': {}",
                    senderUsername, receiverUsername, e.getMessage(), e);
            throw e;
        }
    }
}
