package com.spotifyapi.service.impl;

import com.spotifyapi.dto.TokensDTO;
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
import java.util.ArrayList;
import java.util.List;

import static se.michaelthelin.spotify.enums.AuthorizationScope.*;


@Service
@RequiredArgsConstructor
public class SpotifyAuthImpl implements SpotifyAuth {

    private final SpotifyApi spotifyApi;

    @Override
    public String authorize() {
        return spotifyApi.authorizationCodeUri()
                .scope(USER_READ_EMAIL, USER_TOP_READ, USER_FOLLOW_READ)
                .show_dialog(true)
                .build()
                .execute()
                .toString();
    }

    @Override
    public TokensDTO getAuthorizationTokens(String code) {
        try {

            AuthorizationCodeCredentials credentials = spotifyApi.authorizationCode(code).build().execute();

            spotifyApi.setAccessToken(credentials.getAccessToken());
            spotifyApi.setRefreshToken(credentials.getRefreshToken());

            return new TokensDTO(credentials.getAccessToken(), credentials.getRefreshToken());

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving tokens: " + e.getMessage());
        }
    }

    @Override
    public TokensDTO getNewAccessToken(String refreshToken) {
        try {
            AuthorizationCodeCredentials credentials = spotifyApi.
                    authorizationCodeRefresh()
                    .refresh_token(refreshToken)
                    .build()
                    .execute();

            spotifyApi.setAccessToken(credentials.getAccessToken());

            return new TokensDTO(spotifyApi.getAccessToken(), refreshToken);

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving tokens: " + e.getMessage());
        }
    }
}
