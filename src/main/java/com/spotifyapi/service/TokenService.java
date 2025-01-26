package com.spotifyapi.service;


public interface TokenService {

    String extractAccessToken(String authorizationHeader);

}
