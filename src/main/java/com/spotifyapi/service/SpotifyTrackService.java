package com.spotifyapi.service;

import com.spotifyapi.model.SpotifyTrackFromPlaylist;
import com.spotifyapi.model.SpotifyUserPlaylist;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

public interface SpotifyTrackService {

    void saveTracks(Track track, SpotifyUserPlaylist playlist);

    boolean isAlreadyExist(String trackId, String playlistId);

    Track convertToTrackFormat(TrackSimplified trackSimplified);

}
