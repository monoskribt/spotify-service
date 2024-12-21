package com.spotifyapi.service.impl;


import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.model.SpotifyUserPlaylist;
import com.spotifyapi.model.User;
import com.spotifyapi.repository.PlaylistRepository;
import com.spotifyapi.repository.UserRepository;
import com.spotifyapi.service.SpotifyPlaylistService;
import com.spotifyapi.service.SpotifyTrackService;
import com.spotifyapi.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final SpotifyApi spotifyApi;
    private final UserRepository userRepository;
    private final SpotifyPlaylistService playlistService;
    private final SpotifyTrackService spotifyTrackService;
    private final PlaylistRepository playlistRepository;

    @Transactional
    @Override
    public void saveUserOfData(TokensDTO tokens) {
        try {
            User newUser = new User();

            var userProfile = spotifyApi.getCurrentUsersProfile().build().execute();

            newUser.setUsername(userProfile.getDisplayName());
            newUser.setEmail(userProfile.getEmail());
            newUser.setId(userProfile.getId());

            userRepository.save(newUser);

            List<PlaylistSimplified> playlists = Arrays.stream(spotifyApi
                    .getListOfCurrentUsersPlaylists()
                    .build()
                    .execute().getItems())
                    .toList();

            for (PlaylistSimplified playlist : playlists) {

                playlistService.savePlaylistToDatabase(playlist, newUser);

                SpotifyUserPlaylist spotifyUserPlaylist = playlistRepository.findById(playlist.getId())
                        .orElseThrow(() -> new IllegalStateException("Playlist not found after save"));

                var playlistTracks = Arrays.stream(
                        spotifyApi.getPlaylistsItems(playlist.getId())
                                .build()
                                .execute()
                                .getItems())
                        .toList();

                for (PlaylistTrack trackItem : playlistTracks) {
                    spotifyTrackService.saveTracks((Track) trackItem.getTrack(), spotifyUserPlaylist);
                }

            }


        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }



    @SneakyThrows
    @Override
    public String getCurrentUsername() {
        var profile = spotifyApi.getCurrentUsersProfile().build().execute();
        return profile.getDisplayName();
    }

    @SneakyThrows
    @Override
    public String getCurrentId() {
        var profile = spotifyApi.getCurrentUsersProfile().build().execute();
        return profile.getId();
    }

    @SneakyThrows
    @Override
    public boolean isAlreadyExist() {
         return userRepository.existsByEmail(spotifyApi
                 .getCurrentUsersProfile()
                 .build()
                 .execute().getEmail());
    }
}
