package org.soradium.portablemessengerapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// divide it to make it aligned to SOLID!!!!!
// think of some message/user/group runtime
// caching mechanism that will store
// temporarily users of app that are currently using
// the app.

// do session, security(log in, sign up)
//

// migrate session to Redis? what is redis?
@RestController
@RequestMapping("/api")
public class MainController {

    @GetMapping("/firstmap")
    public String firstMapping() {
        return "Hello world!";
    }


}
