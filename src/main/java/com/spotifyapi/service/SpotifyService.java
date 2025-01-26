package com.spotifyapi.service;

import com.spotifyapi.model.SpotifyArtist;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;
import java.util.Set;

public interface SpotifyService {

    Set<PlaylistSimplified> getOfUsersPlaylists(String authorizationHeader);

    List<SpotifyArtist> getFollowedArtist(String authorizationHeader);

    List<AlbumSimplified> getReleases(String authorizationHeader);

    List<AlbumSimplified> getReleases(Long releaseOfDay, String authorizationHeader);

    String saveReleasesToPlaylistById(String playlistId, Long releaseOfDay, String authorizationHeader);

    String deleteAllOfTracksFromPlaylistById(String playlistId, String authorizationHeader);
}
