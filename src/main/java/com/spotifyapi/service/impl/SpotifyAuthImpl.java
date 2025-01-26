package com.spotifyapi.service.impl;

import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.service.SpotifyAuth;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.io.IOException;

import static se.michaelthelin.spotify.enums.AuthorizationScope.*;


@Service
@RequiredArgsConstructor
public class SpotifyAuthImpl implements SpotifyAuth {

    private final SpotifyApi spotifyApi;

    @Override
    public String authorize() {
        return spotifyApi.authorizationCodeUri()
                .scope(USER_LIBRARY_READ,
                        USER_LIBRARY_MODIFY,
                        USER_FOLLOW_READ,
                        USER_FOLLOW_MODIFY,
                        PLAYLIST_MODIFY_PRIVATE,
                        PLAYLIST_MODIFY_PUBLIC,
                        USER_READ_EMAIL)
                .show_dialog(true)
                .build()
                .execute()
                .toString();
    }

    @Override
    public TokensDTO getAuthorizationTokens(String code) {
        try {

            AuthorizationCodeCredentials credentials = spotifyApi.authorizationCode(code).build().execute();

            String accessToken = credentials.getAccessToken();
            String refreshToken = credentials.getRefreshToken();

            spotifyApi.setAccessToken(accessToken);

            return new TokensDTO(accessToken, refreshToken);

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

            String accessToken = credentials.getAccessToken();

            spotifyApi.setAccessToken(accessToken);

            return new TokensDTO(accessToken, refreshToken);

        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving tokens: " + e.getMessage());
        }
    }
}
