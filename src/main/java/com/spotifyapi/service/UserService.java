package com.spotifyapi.service;

import com.spotifyapi.dto.TokensDTO;

public interface UserService {

    void saveUserOfData(TokensDTO tokens);

    String getCurrentUsername();

    String getCurrentId();

    String getCurrentEmail();

    boolean isAlreadyExist();
}
