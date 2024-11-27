package com.spotifyapi.service.impl;

import com.spotifyapi.service.SpotifyAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.AuthorizationScope;


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
}
