package com.spotifyapi.service.impl;

import com.spotifyapi.model.SpotifyUserPlaylist;
import com.spotifyapi.model.User;
import com.spotifyapi.repository.PlaylistRepository;
import com.spotifyapi.service.PlaylistService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

@Service
@AllArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SpotifyApi spotifyApi;

    @SneakyThrows
    @Override
    public boolean isAlreadyExistById(String playlistId) {
        return playlistRepository.existsById(playlistId);
    }


}
