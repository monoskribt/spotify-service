package com.spotifyapi.service;

import com.spotifyapi.dto.spotify_entity.SpotifyPlaylistsDTO;
import com.spotifyapi.model.SpotifyArtist;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;
import java.util.Set;

public interface SpotifyService {

    <T> List<T> getFollowedArtist(String authorizationHeader, Class<T> returnTypeOfClass);

    Set<SpotifyPlaylistsDTO> getOfUsersPlaylists(String authorizationHeader);

    List<AlbumSimplified> getReleases(String authorizationHeader);

    <T> List<T> getReleases(String authorizationHeader, Long releaseOfDay, Class<T> returnTypeOfClass);

    String saveReleasesToPlaylistById(String authorizationHeader, String playlistId, Long releaseOfDay);

    String deleteAllOfTracksFromPlaylistById(String authorizationHeader, String playlistId);
}