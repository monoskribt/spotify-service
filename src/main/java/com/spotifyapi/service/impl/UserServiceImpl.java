package com.spotifyapi.service.impl;


import com.spotifyapi.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final SpotifyApi spotifyApi;

    @Override
    public String getUserData() {
        try {
            var userProfile = spotifyApi.getCurrentUsersProfile().build().execute();
            return "You are get your data successfully! \n"
                    + "User name: " + userProfile.getDisplayName() + " \n"
                    + "Email: " + userProfile.getEmail() + " \n"
                    + "Id: " + userProfile.getId() + " \n";
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

}
