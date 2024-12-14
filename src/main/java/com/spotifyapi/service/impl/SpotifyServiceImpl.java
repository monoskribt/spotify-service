package com.spotifyapi.service.impl;


import com.google.gson.JsonArray;
import com.spotifyapi.model.SpotifyArtist;
import com.spotifyapi.service.SpotifyService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@AllArgsConstructor
public class SpotifyServiceImpl implements SpotifyService {

    private final SpotifyApi spotifyApi;

    @Override
    @SneakyThrows
    public List<SpotifyArtist> getFollowedArtist() {

        return Arrays.stream(spotifyApi
                .getUsersFollowedArtists(ModelObjectType.ARTIST)
                .limit(10)
                .build()
                .execute()
                .getItems())
                .map(artist -> new SpotifyArtist(artist.getId(), artist.getName()))
                .toList();

    }

    @Override
    @SneakyThrows
    public List<AlbumSimplified> getReleases() {
        List<SpotifyArtist> artists = getFollowedArtist();

        List<AlbumSimplified> listOfAlbums = new ArrayList<>();

        LocalDate sixMothAgo = LocalDate.now().minusMonths(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for(SpotifyArtist artist : artists) {
            var album = Arrays.stream(spotifyApi
                    .getArtistsAlbums(artist.getId())
                    .build()
                    .execute()
                    .getItems())
                            .filter(date -> {
                                LocalDate releaseDate = LocalDate.parse(date.getReleaseDate(), formatter);
                                return releaseDate.isAfter(sixMothAgo);
                            }).toList();

            listOfAlbums.addAll(album);
        }

        return listOfAlbums
                .stream()
                .sorted(Comparator.comparing(AlbumSimplified::getReleaseDate))
                .toList();
    }

    @SneakyThrows
    @Override
    public List<PlaylistSimplified> getOfUserPlaylists() {
        return Arrays.stream(spotifyApi.getListOfCurrentUsersPlaylists()
                .build()
                .execute().getItems()).toList();
    }

    @SneakyThrows
    @Override
    public void saveReleasesToPlaylistById(String playlistId) {
        List<AlbumSimplified> releases = getReleases();
        List<String> trackUrl = new ArrayList<>();

        for(AlbumSimplified album : releases) {
            var track = spotifyApi.getAlbumsTracks(album.getId())
                    .build()
                    .execute()
                    .getItems();
            Arrays.stream(track).map(TrackSimplified::getUri)
                    .forEach(trackUrl::add);
        }

        spotifyApi.addItemsToPlaylist(playlistId, trackUrl.toArray(new String[0]))
                .build()
                .execute();
    }

    @Override
    public void deleteAllOfTracksFromPlaylistById(String playlistId) {
        //TODO
    }


}
