package com.spotifyapi.service.impl;


import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.model.SpotifyUserPlaylist;
import com.spotifyapi.model.User;
import com.spotifyapi.repository.PlaylistRepository;
import com.spotifyapi.repository.UserRepository;
import com.spotifyapi.service.PlaylistService;
import com.spotifyapi.service.UserService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final SpotifyApi spotifyApi;
    private final UserRepository userRepository;
    private final PlaylistService playlistService;
    private final PlaylistRepository playlistRepository;

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

            for(PlaylistSimplified playlist : playlists) {
                if(!playlistService.isAlreadyExistById(playlist.getId())) {
                    SpotifyUserPlaylist spotifyUserPlaylist = new SpotifyUserPlaylist();

                    spotifyUserPlaylist.setId(playlist.getId());
                    spotifyUserPlaylist.setName(playlist.getName());
                    spotifyUserPlaylist.setExternalUrl(playlist.getExternalUrls().get("spotify"));
                    spotifyUserPlaylist.setOwnerName(playlist.getOwner().getDisplayName());
                    spotifyUserPlaylist.setSnapshotId(playlist.getSnapshotId());

                    spotifyUserPlaylist.setUser(newUser);

                    playlistRepository.save(spotifyUserPlaylist);
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
