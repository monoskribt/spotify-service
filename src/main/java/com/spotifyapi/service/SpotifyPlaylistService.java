package com.spotifyapi.service;

import com.spotifyapi.model.SpotifyUserPlaylist;
import com.spotifyapi.model.User;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

public interface SpotifyPlaylistService {

    boolean isAlreadyExistById(String playlistId);

    SpotifyUserPlaylist savePlaylistToDatabase(PlaylistSimplified playlist, User user);

}
