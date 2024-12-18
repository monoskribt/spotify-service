package com.spotifyapi.service;

import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

public interface PlaylistService {

    boolean isAlreadyExistById(String playlistId);

}
