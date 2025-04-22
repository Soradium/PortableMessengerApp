package org.soradium.portablemessengerapp.controller;

import org.soradium.portablemessengerapp.dto.UsernameAsObjectDto;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.service.UserServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

@Controller
public class SignupController {
    // сайнап контроллер нужен для обработки сообщения от фронтапи
    // что он зарегал нового юзера (в базу тут то надо записаться для энтитей)
    private UserServiceImpl service;

    public SignupController(UserServiceImpl service) {
        this.service = service;
    }

    @KafkaListener(
            id="add_new_user",
            topics = "auth-send",
            containerFactory = "kafkaListenerUsernameAsDtoContainerFactory"
    )
    public void addNewUser(UsernameAsObjectDto user) {
        User u = new User();
        u.setUsername(user.username());
        service.createUser(u);
    }
}
