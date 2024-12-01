package com.spotifyapi.service.impl;


import com.spotifyapi.model.User;
import com.spotifyapi.service.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl {

    private final SpotifyApi spotifyApi;
    private final UserRepository userRepository;

    public void saveUserData(List<String> tokens) {
        try {
            User newUser = new User();

            var userProfile = spotifyApi.getCurrentUsersProfile().build().execute();

            newUser.setUsername(userProfile.getDisplayName());
            newUser.setEmail(userProfile.getEmail());
            newUser.setSpotifyUserId(userProfile.getId());
            newUser.setAccessToken(tokens.get(0));
            newUser.setRefreshToken(tokens.get(1));

            userRepository.save(newUser);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving user data: " + e.getMessage());
        }
    }
}
