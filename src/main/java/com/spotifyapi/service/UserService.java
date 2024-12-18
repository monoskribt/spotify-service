package com.spotifyapi.service;

import com.spotifyapi.dto.TokensDTO;

public interface UserService {

    void saveUserOfData(TokensDTO tokens);

    String getCurrentUsername();

    String getCurrentId();

    boolean isAlreadyExist();
}
