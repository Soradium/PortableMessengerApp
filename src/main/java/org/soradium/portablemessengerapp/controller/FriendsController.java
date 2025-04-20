package org.soradium.portablemessengerapp.controller;

import jakarta.servlet.http.HttpSession;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friends")
public class FriendsController {

    private final UserServiceImpl userService;

    public FriendsController(
            @Autowired UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/add-friend")
    public ResponseEntity<String> addFriend(
            @RequestBody String username,
            HttpSession session) {
        // 1. check if there is such a user - do not lose the result - keep it.
        // 2. if there is not - return a bad request.
        // 3. if there is - add new friend to our friend - hibernate should
        // be able to manage the M-M connection that we have.
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
        User friendUser =
                userService.getUserByUsername(friendName);
        if (friendUser == null) {
            return new ResponseEntity<>("No such user was found.",
                    HttpStatus.BAD_REQUEST);
        }
        User currentUser =
                userService.getUserWithFriendsByUsername(ud.getUsername());
        if (currentUser.getFriends().contains(friendUser)) {
            return new ResponseEntity<>("You already have this friend " +
                    "in your friends list.",
                    HttpStatus.BAD_REQUEST);
        }
        currentUser.getFriends().add(friendUser);
        userService.updateUser(currentUser);
        // will this also add at the other side?
        return new ResponseEntity<>("Successfully added friend.",
                HttpStatus.CREATED); //added friend successfully
    }

    // add friend
    // enter a chat with a friend

}
