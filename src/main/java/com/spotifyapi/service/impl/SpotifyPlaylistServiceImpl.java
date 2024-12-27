package com.spotifyapi.service.impl;

import com.spotifyapi.model.SpotifyUserPlaylist;
import com.spotifyapi.model.User;
import com.spotifyapi.repository.PlaylistRepository;
import com.spotifyapi.service.SpotifyPlaylistService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;


@Service
@AllArgsConstructor
public class SpotifyPlaylistServiceImpl implements SpotifyPlaylistService {

    private final PlaylistRepository playlistRepository;

    @SneakyThrows
    @Override
    public boolean isAlreadyExistById(String playlistId) {
        return playlistRepository.existsById(playlistId);
    }


    @SneakyThrows
    @Override
    public SpotifyUserPlaylist savePlaylistToDatabase(PlaylistSimplified playlist, User user) {
        if(!isAlreadyExistById(playlist.getId())) {
            SpotifyUserPlaylist spotifyUserPlaylist = new SpotifyUserPlaylist();

            spotifyUserPlaylist.setId(playlist.getId());
            spotifyUserPlaylist.setName(playlist.getName());
            spotifyUserPlaylist.setExternalUrl(playlist.getExternalUrls().get("spotify"));
            spotifyUserPlaylist.setOwnerName(playlist.getOwner().getDisplayName());
            spotifyUserPlaylist.setSnapshotId(playlist.getSnapshotId());

            spotifyUserPlaylist.setUser(user);

            playlistRepository.save(spotifyUserPlaylist);
        }
        return null;
    }
}
