package com.spotifyapi.service.impl;

import com.spotifyapi.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
public class TokenServiceImpl implements TokenService {


    @Override
    public String extractAccessToken(String authorizationHeader) {
        return Optional.ofNullable(authorizationHeader)
                .filter(token -> token.startsWith("Bearer "))
                .map(header -> header.substring(7))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Authorization header"));
    }
}
