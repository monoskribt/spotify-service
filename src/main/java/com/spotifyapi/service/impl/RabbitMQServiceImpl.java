package com.spotifyapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyapi.model.SpotifyRelease;
import com.spotifyapi.model.User;
import com.spotifyapi.service.RabbitMQService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RabbitMQServiceImpl implements RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    private final Binding binding;
    private final ObjectMapper objectMapper;


    @SneakyThrows
    @Override
    public void sendMessageToTelegram(User user, Set<SpotifyRelease> releases) {
        var releaseInfo = releases.stream()
                .map(album -> Map.of(
                        "albumId", album.getId(),
                        "albumName", album.getName()
                ))
                .toList();

        Map<String, Object> message = Map.of(
                "email", user.getEmail(),
                "release", releaseInfo
        );

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(binding.getExchange(),
                    binding.getRoutingKey(), jsonMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

