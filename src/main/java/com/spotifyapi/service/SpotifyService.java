package com.spotifyapi.service;

import com.spotifyapi.model.SpotifyArtist;

import java.util.List;

public interface SpotifyService {

    List<SpotifyArtist> getFollowedArtist();

}
