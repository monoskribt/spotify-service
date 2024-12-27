package com.spotifyapi.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotifyapi.props.RabbitMQProperties;
import com.spotifyapi.service.RabbitMQService;
import com.spotifyapi.service.SpotifyService;
import com.spotifyapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RabbitMQServiceImpl implements RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    private final SpotifyService spotifyService;
    private final UserService userService;
    private final Binding binding;


    @SneakyThrows
    @Override
    public void sendInfoToTelegram() {
        List<AlbumSimplified> albumList = spotifyService.getReleases();

        var releaseInfo = albumList.stream()
                .map(album -> Map.of(
                        "albumId", album.getId(),
                        "albumName", album.getName()
                )).toList();

        Map<String, Object> message = Map.of(
                "email", userService.getCurrentEmail(),
                "release", releaseInfo
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString(message);

        rabbitTemplate.convertAndSend(binding.getExchange(),
                binding.getRoutingKey(), jsonMessage);
    }

}
