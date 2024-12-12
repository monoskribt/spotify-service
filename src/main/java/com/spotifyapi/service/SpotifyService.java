package com.spotifyapi.service;

import com.spotifyapi.model.SpotifyArtist;
import com.spotifyapi.model.SpotifyRealises;

import java.util.List;

public interface SpotifyService {

    List<SpotifyArtist> getFollowedArtist();

    List<SpotifyRealises> getRealises();

}
