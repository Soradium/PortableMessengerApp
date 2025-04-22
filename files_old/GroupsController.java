//package org.soradium.portablemessengerapp.controller;
//
//import org.soradium.portablemessengerapp.entity.Group;
//import org.soradium.portablemessengerapp.service.GroupServiceImpl;
//import org.soradium.portablemessengerapp.service.UserServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.context.SecurityContextHolderStrategy;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/groups")
//public class GroupsController {
//    @Autowired
//    private GroupServiceImpl service;
//    @Autowired
//    private UserServiceImpl userService;
//    // don't forget to add the add person to group functionality later
//
//    @PostMapping("/create-group")
//    public ResponseEntity<String> createGroup(
//            @RequestBody String groupName) {
//        Group g = service.getGroupByName(groupName);
//        if (g != null) {
//            return new ResponseEntity<>(
//                    "Group with this name already exists.",
//                    HttpStatus.BAD_REQUEST
//            );
//        }
//
//        SecurityContextHolderStrategy securityContextHolderStrategy
//                = SecurityContextHolder.getContextHolderStrategy();
//        UserDetails ud = (UserDetails) securityContextHolderStrategy
//                .getContext()
//                .getAuthentication()
//                .getPrincipal();
//        g = new Group();
//        g.setAdmin(userService
//                .getUserByUsername(
//                        ud.getUsername()));
//        g.setGroupName(groupName);
//        service.createGroup(g);
//        return new ResponseEntity<>(
//                "Group created successfully",
//                HttpStatus.CREATED
//        );
//    }
//
//    public GroupServiceImpl getService() {
//        return service;
//    }
//
//    //Add custom exceptions! (Contoller advices)
//    @Autowired
//    public void setService(GroupServiceImpl service)
//            throws Exception {
//        if (service == null) {
//            throw new Exception("No group service found!");
//        }
//        this.service = service;
//    }
//}
