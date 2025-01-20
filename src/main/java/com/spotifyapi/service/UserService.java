package com.spotifyapi.service;

import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.enums.SubscribeStatus;
import com.spotifyapi.model.User;

import java.util.Set;

public interface UserService {

    void saveUserOfData(TokensDTO tokens);

    void manageSubscribeStatusOfUser(SubscribeStatus status);

    Set<User> getAllUsersWithSubscribeStatus();

    String getSubscribeStatusUsers();

    String getCurrentUsername();

    String getCurrentId();

    boolean isAlreadyExist();
}
