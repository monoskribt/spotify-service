package com.spotifyapi.service.impl;

import com.spotifyapi.service.SpotifyAuth;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.AuthorizationScope;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class SpotifyAuthImpl implements SpotifyAuth {

    private final SpotifyApi spotifyApi;

    @Override
    public String authorize() {
        return spotifyApi.authorizationCodeUri()
                .scope(AuthorizationScope.USER_READ_EMAIL,
                        AuthorizationScope.USER_TOP_READ)
                .show_dialog(true)
                .build()
                .execute()
                .toString();
    }

    @Override
    public void setAccessToken(String code) {
        try {
            AuthorizationCodeCredentials credentials = spotifyApi.authorizationCode(code).build().execute();
            String accessToken = credentials.getAccessToken();
            String refreshToken = credentials.getRefreshToken();
            spotifyApi.setAccessToken(accessToken);

            System.out.println(accessToken);
            System.out.println(refreshToken);

            ResponseEntity.status(HttpStatusCode.valueOf(200)).build();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
