package com.spotifyapi.controller;

import com.spotifyapi.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RabbitController {

    private final RabbitMQService rabbitMQService;

    @GetMapping("/test-rabbitmq")
    public String testRabbitMQ() {
        rabbitMQService.sendInfoToTelegram();
        return "Message sent to RabbitMQ!";
    }

}
