package com.spotifyapi.controller;

import com.spotifyapi.service.RabbitMQService;
import com.spotifyapi.service.SpotifyReleaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RabbitController {

    private final SpotifyReleaseService spotifyReleaseService;

    @GetMapping("/test-rabbitmq")
    public String testRabbitMQ() {
        spotifyReleaseService.checkReleasesForAllUsers();
        return "Message sent to RabbitMQ!";
    }

}
