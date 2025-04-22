package org.soradium.portablemessengerapp.controller;

import org.soradium.portablemessengerapp.dto.FriendRequestResponseDto;
import org.soradium.portablemessengerapp.dto.FriendRequestSenderAndReceiverDto;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class FriendsController {

    private final UserServiceImpl userService;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    // move into thread pools, work with requests as futures

    @Autowired
    public FriendsController(
            UserServiceImpl userService,
            KafkaTemplate<String, Object> kafkaMessageTemplate) {
        this.userService = userService;
        this.kafkaTemplate = kafkaMessageTemplate;
    }

    // WILL BECOME KAFKA LISTENER
    @KafkaListener(
            id = "add_friend",
            topics = "friend-add",
            containerFactory = "kafkaListenerFriendRequestContainerFactory"
    )
    public void addFriend(FriendRequestSenderAndReceiverDto request) {
        String senderUsername = request.senderUsername();
        String receiverUsername = request.receiverUsername();
        User friendUser =
                userService.getUserByUsername(receiverUsername);
        if (friendUser == null) {
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

        kafkaTemplate.send(
                "friend-response",
                new FriendRequestResponseDto(senderUsername,
                        "Successfully requested friend "
                                + receiverUsername)
        );

    }


    // 1. check if there is such a user - do not lose the result - keep it.
    // 2. if there is not - return a bad request.
    // 3. if there is - add new friend to our friend - hibernate should
    // be able to manage the M-M connection that we have.

        /*
       if (username == null) {
            return new ResponseEntity<>("Error passing friend username.",
                    HttpStatus.BAD_REQUEST);
        }
        SecurityContextHolderStrategy securityContextHolderStrategy
                = SecurityContextHolder.getContextHolderStrategy();
        String friendName = username.substring(1, username.length() - 1);
        UserDetails ud
                = (UserDetails) securityContextHolderStrategy
                .getContext()
                .getAuthentication()
                .getPrincipal();
        if (friendName.equals(ud.getUsername())) {
            return new ResponseEntity<>("You can't add yourself.",
                    HttpStatus.BAD_REQUEST);
        }
        */

//        /// TOP IS FRONTEND API, REMAKE BOTTOM WITH KAFKA REPLY
//        User friendUser =
//                userService.getUserByUsername(friendName);
//        if (friendUser == null) {
//            return new ResponseEntity<>("No such user was found.",
//                    HttpStatus.BAD_REQUEST);
//        }
//        User currentUser =
//                userService.getUserWithFriendsByUsername(ud.getUsername());
//        if (currentUser.getFriends().contains(friendUser)) {
//            return new ResponseEntity<>("You already have this friend " +
//                    "in your friends list.",
//                    HttpStatus.BAD_REQUEST);
//        }
//        currentUser.getFriends().add(friendUser);
//        userService.updateUser(currentUser);
//        // will this also add at the other side?
//        return new ResponseEntity<>("Successfully added friend.",

//                HttpStatus.CREATED); //added friend successfully


    // add friend
    // enter a chat with a friend

}
