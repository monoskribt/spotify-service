package com.spotifyapi.service.impl;


import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.enums.SubscribeStatus;
import com.spotifyapi.exception.UserNotFoundException;
import com.spotifyapi.mapper.TrackWrapper;
import com.spotifyapi.model.SpotifyTrackFromPlaylist;
import com.spotifyapi.model.SpotifyUserPlaylist;
import com.spotifyapi.model.User;
import com.spotifyapi.repository.PlaylistRepository;
import com.spotifyapi.repository.TrackRepository;
import com.spotifyapi.repository.UserRepository;
import com.spotifyapi.service.SpotifyPlaylistService;
import com.spotifyapi.service.SpotifyTrackService;
import com.spotifyapi.service.UserService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final SpotifyApi spotifyApi;
    private final UserRepository userRepository;
    private final SpotifyPlaylistService playlistService;
    private final SpotifyTrackService spotifyTrackService;
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;

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

            List<SpotifyTrackFromPlaylist> saveTrackToDB = new ArrayList<>();

            for (PlaylistSimplified playlist : playlists) {

                playlistService.savePlaylistToDatabase(playlist, newUser);

                SpotifyUserPlaylist spotifyUserPlaylist = playlistRepository.findById(playlist.getId())
                        .orElseThrow(() -> new IllegalStateException("Playlist not found after save"));

                List<PlaylistTrack> playlistTracks = Arrays.stream(
                        spotifyApi.getPlaylistsItems(playlist.getId())
                                .build()
                                .execute()
                                .getItems())
                        .toList();

                for (PlaylistTrack trackItem : playlistTracks) {
                    if(trackItem != null) {
                        Track track = (Track) trackItem.getTrack();

                        SpotifyTrackFromPlaylist trackEntity = spotifyTrackService
                                .convertTrackToTrackDBEntity(new TrackWrapper(track), spotifyUserPlaylist);

                        saveTrackToDB.add(trackEntity);
                    }
                }

            }

            trackRepository.saveAll(saveTrackToDB);


        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void manageSubscribeStatusOfUser(SubscribeStatus status) {
        User user = userRepository.findById(getCurrentId())
                .orElseThrow(() -> new UserNotFoundException("User is not found"));

        user.setSubscribeStatus(status);
        userRepository.save(user);
    }

    @Override
    public Set<User> getAllUsersWithSubscribeStatus() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getSubscribeStatus().equals(SubscribeStatus.SUBSCRIBE))
                .collect(Collectors.toSet());
    }

    @Override
    public String getSubscribeStatusUsers() {
        User u = userRepository.findById(getCurrentId())
                .orElseThrow(() -> new UserNotFoundException("User is not found"));
        return u.getSubscribeStatus().toString();
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
