package com.spotifyapi.service;


public interface SpotifyAuth {

    String authorize();

    void setAccessToken(String code);

}
