package com.spotifyapi.service;


public interface SpotifyAuth {

    String authorize();

    void getAndSetAccessToken(String code);

}
