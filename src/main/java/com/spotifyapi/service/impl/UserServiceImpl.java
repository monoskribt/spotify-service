package com.spotifyapi.service.impl;


import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.model.User;
import com.spotifyapi.repository.UserRepository;
import com.spotifyapi.service.UserService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final SpotifyApi spotifyApi;
    private final UserRepository userRepository;

    @Override
    public void saveUserData(TokensDTO tokens) {
        try {
            User newUser = new User();

            var userProfile = spotifyApi.getCurrentUsersProfile().build().execute();

            newUser.setUsername(userProfile.getDisplayName());
            newUser.setEmail(userProfile.getEmail());
            newUser.setSpotifyUserId(userProfile.getId());

            userRepository.save(newUser);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }
}
