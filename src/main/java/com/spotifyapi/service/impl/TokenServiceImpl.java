package com.spotifyapi.service.impl;

import com.spotifyapi.model.User;
import com.spotifyapi.props.SpotifyProps;
import com.spotifyapi.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import se.michaelthelin.spotify.SpotifyApi;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final SpotifyApi spotifyApi;
    private final SpotifyProps spotifyProps;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String extractAccessToken(String authorizationHeader) {
        return Optional.ofNullable(authorizationHeader)
                .filter(token -> token.startsWith("Bearer "))
                .map(header -> header.substring(7))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authorization header"));
    }


    @Override
    public boolean isValidAccessToken(User u) {
        if (u.getAccessToken().isEmpty() || u.getExpiresAccessTokenAt() == null) {
            return false;
        }
        return Instant.now().isBefore(u.getExpiresAccessTokenAt());
    }
}
