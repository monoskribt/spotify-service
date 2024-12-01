package com.spotifyapi.service;


import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.util.List;

public interface SpotifyAuth {

    String authorize();

    List<String> getAuthorizationTokens(String code);

}
