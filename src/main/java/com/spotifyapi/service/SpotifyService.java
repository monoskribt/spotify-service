package com.spotifyapi.service;

import com.spotifyapi.model.SpotifyArtist;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;
import java.util.Set;

public interface SpotifyService {

    Set<PlaylistSimplified> getOfUsersPlaylists();

    List<SpotifyArtist> getFollowedArtist();

    List<AlbumSimplified> getReleases();

    List<AlbumSimplified> getReleases(Long releaseOfDay);

    void saveReleasesToPlaylistById(String playlistId, Long releaseOfDay);

    void deleteAllOfTracksFromPlaylistById(String playlistId);
}
