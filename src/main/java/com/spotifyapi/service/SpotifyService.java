package com.spotifyapi.service;

import com.spotifyapi.model.SpotifyArtist;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;

public interface SpotifyService {

    List<SpotifyArtist> getFollowedArtist();

    List<AlbumSimplified> getReleases();

    List<PlaylistSimplified> getOfUserPlaylists();

    void saveReleasesToMyPlaylist(String playlistId);

}
