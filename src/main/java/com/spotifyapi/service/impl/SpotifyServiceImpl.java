package com.spotifyapi.service.impl;


import com.spotifyapi.model.SpotifyArtist;
import com.spotifyapi.model.SpotifyRealises;
import com.spotifyapi.service.SpotifyService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<SpotifyRealises> getRealises() {
        List<SpotifyArtist> artists = getFollowedArtist();

        List<SpotifyRealises> listOfAlbums = new ArrayList<>();

        LocalDate sixMothAgo = LocalDate.now().minusMonths(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for(SpotifyArtist artist : artists) {
            Paging<AlbumSimplified> album = spotifyApi
                    .getArtistsAlbums(artist.getId())
                    .build()
                    .execute();

            Arrays.stream(album.getItems())
                    .filter(items -> {
                        LocalDate releaseDate = LocalDate.parse(items.getReleaseDate(), formatter);
                        return releaseDate.isAfter(sixMothAgo);
                    })
                    .forEach(el -> {
                        SpotifyRealises realises = new SpotifyRealises();
                        realises.setId(el.getId());
                        realises.setNameOfGroup(Arrays.stream(el.getArtists())
                                .map(ArtistSimplified::getName)
                                .collect(Collectors.joining(", ")));
                        realises.setTitle(el.getName());
                        realises.setDate(el.getReleaseDate());
                        listOfAlbums.add(realises);
                    });
        }
        return listOfAlbums;
    }


}
