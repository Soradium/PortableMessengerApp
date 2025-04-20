package org.soradium.portablemessengerapp.controller;

import org.soradium.portablemessengerapp.dto.GroupUserChatTargetDtoRequest;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.service.MessageServiceImpl;
import org.soradium.portablemessengerapp.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class SidebarUiController {

    @Autowired
    private MessageServiceImpl messageService;
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/find-target")
    public ResponseEntity<?> getTargetToOpenChatWith(
            @RequestBody GroupUserChatTargetDtoRequest target) {
        if (target.targetType().equals("GROUP")) {
            // GROUP FUNCTIONALITY IS LATER TO BE ADDED - EX.COMMENTARY IS DOWN BELOW
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else if (target.targetType().equals("USER")) {
            UserDetails ud = (UserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
            User senderUser = userService.getUserWithFriendsByUsername(ud.getUsername());
            User targetUser
                    = userService
                    .getUserByUsername(target.target());
            if (!senderUser.getFriends().contains(targetUser)) {
                return new ResponseEntity<>("Can't access a particular user. "
                        + targetUser
                        + " is not a friend of " + senderUser,
                        HttpStatus.NO_CONTENT);
            }
            if (targetUser == null) {
                // think about turning this
                // into exception in controller advice - it can be used from
                // various places
                return new ResponseEntity<>(
                        "No such entity was found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(
                    "targetUser found! " + target.target(),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
///  GROUP EX. FUNCTIONALITY - RESTORE LATER ///
//    @Autowired
//    private GroupServiceImpl groupService;
//            Group group
//                    = groupService
//                    .getGroupByName(target.target());
//            if (group == null) { // think about turning this
//                // into exception in controller advice - it can be used from
//                // various places
//                return new ResponseEntity<>(
//                        "No such entity was found", HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>(
//                    "Group found! " + target.target(),
//                    HttpStatus.OK
//            );