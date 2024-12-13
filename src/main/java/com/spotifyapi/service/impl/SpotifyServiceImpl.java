package com.spotifyapi.service.impl;


import com.spotifyapi.model.SpotifyArtist;
import com.spotifyapi.model.SpotifyReleases;
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
import java.util.Comparator;
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
    public List<SpotifyReleases> getReleases() {
        List<SpotifyArtist> artists = getFollowedArtist();

        List<SpotifyReleases> listOfAlbums = new ArrayList<>();

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
                    .limit(7)
                    .forEach(el -> {
                        SpotifyReleases releases = new SpotifyReleases();
                        releases.setId(el.getId());
                        releases.setNameOfGroup(Arrays.stream(el.getArtists())
                                .map(ArtistSimplified::getName)
                                .collect(Collectors.joining(", ")));
                        releases.setTitle(el.getName());
                        releases.setDate(el.getReleaseDate());
                        listOfAlbums.add(releases);
                    });
        }
        return listOfAlbums.stream()
                .sorted(Comparator.comparing(SpotifyReleases::getDate).thenComparing(SpotifyReleases::getNameOfGroup)).collect(Collectors.toList());
    }


}
