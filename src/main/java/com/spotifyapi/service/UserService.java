package com.spotifyapi.service;

import com.spotifyapi.dto.TokensDTO;

public interface UserService {

    void saveUserData(TokensDTO tokens);

    String getCurrentUsername();

    String getCurrentId();
}
