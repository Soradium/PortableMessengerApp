package org.soradium.portablemessengerapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.soradium.portablemessengerapp.dto.UsernameAsObjectDto;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class SignupController {
    // сайнап контроллер нужен для обработки сообщения от фронтапи
    // что он зарегал нового юзера (в базу тут то надо записаться для энтитей)
    private final UserService service;

    public SignupController(UserService service) {
        this.service = service;
    }

    @KafkaListener(
            id = "add_new_user",
            topics = "auth-send",
            containerFactory = "kafkaListenerUsernameAsDtoContainerFactory"
    )
    public void addNewUser(UsernameAsObjectDto user) {
        log.debug("Attempting to add user: {}", user.username());
        try {
            User u = new User();
            u.setUsername(user.username());
            service.createUser(u);
            log.debug("Added user successfully: {}", user.username());
        } catch (Exception e) {
            log.error("User insertion failed, name: {}, exception: {}",
                    user.username(), e.getMessage());
            throw e;
        }
    }
}
