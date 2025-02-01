package com.spotifyapi.service;


import com.spotifyapi.model.User;

public interface TokenService {

    String extractAccessToken(String authorizationHeader);

    boolean isValidAccessToken(User u);

}
