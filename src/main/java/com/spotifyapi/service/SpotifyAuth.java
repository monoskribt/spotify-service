package com.spotifyapi.service;


import com.spotifyapi.dto.TokensDTO;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.util.List;

public interface SpotifyAuth {

    String authorize();

    TokensDTO getAuthorizationTokens(String code);

}
