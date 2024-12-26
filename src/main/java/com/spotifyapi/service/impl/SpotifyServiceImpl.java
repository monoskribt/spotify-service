package com.spotifyapi.service.impl;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spotifyapi.mapper.TrackSimplifiedWrapper;
import com.spotifyapi.model.SpotifyArtist;
import com.spotifyapi.model.SpotifyTrackFromPlaylist;
import com.spotifyapi.model.SpotifyUserPlaylist;
import com.spotifyapi.repository.PlaylistRepository;
import com.spotifyapi.repository.TrackRepository;
import com.spotifyapi.service.SpotifyService;
import com.spotifyapi.service.SpotifyTrackService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.spotifyapi.constant.ConstantDayForReleases.THIRTY_DAYS;

@Service
@AllArgsConstructor
public class SpotifyServiceImpl implements SpotifyService {

    private final SpotifyApi spotifyApi;
    private final PlaylistRepository playlistRepository;
    private final SpotifyTrackService spotifyTrackService;
    private final TrackRepository trackRepository;

    @SneakyThrows
    @Override
    public Set<PlaylistSimplified> getOfUsersPlaylists() {
        return Arrays.stream(spotifyApi.getListOfCurrentUsersPlaylists()
                .build()
                .execute().getItems()).collect(Collectors.toSet());
    }

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
        return getReleases(THIRTY_DAYS);
    }

    @Override
    @SneakyThrows
    public List<AlbumSimplified> getReleases(Long releaseOfDay) {
        List<SpotifyArtist> artists = getFollowedArtist();

        List<AlbumSimplified> listOfAlbums = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for(SpotifyArtist artist : artists) {
            var album = Arrays.stream(spotifyApi
                    .getArtistsAlbums(artist.getId())
                    .build()
                    .execute()
                    .getItems())
                            .filter(date -> {
                                LocalDate releaseDate = LocalDate.parse(date.getReleaseDate(), formatter);
                                return releaseDate.isAfter(LocalDate.now().minusDays(releaseOfDay));
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
    public void saveReleasesToPlaylistById(String playlistId, Long releaseOfDay) {
        List<AlbumSimplified> releases = getReleases(releaseOfDay);
        List<String> trackUrl = new ArrayList<>();
        List<SpotifyTrackFromPlaylist> saveTrackToDB = new ArrayList<>();

        Optional<SpotifyUserPlaylist> playlist = playlistRepository.findById(playlistId);

        Set<String> existingTrackIds = spotifyTrackService
                .getExistingTrackIds(playlist.get().getId());

        for (AlbumSimplified album : releases) {
            var tracks = spotifyApi.getAlbumsTracks(album.getId())
                    .build()
                    .execute()
                    .getItems();

            for (TrackSimplified track : tracks) {
                if(track != null && !existingTrackIds.contains(track.getId())) {
                    trackUrl.add(track.getUri());

                    SpotifyTrackFromPlaylist trackEntity = spotifyTrackService
                            .convertTrackToTrackDBEntity(new TrackSimplifiedWrapper(track), playlist.get());

                    saveTrackToDB.add(trackEntity);
                }
            }
        }

        trackRepository.saveAll(saveTrackToDB);

        spotifyApi.addItemsToPlaylist(playlistId, trackUrl.toArray(new String[0]))
                .build()
                .execute();
    }

    @SneakyThrows
    @Override
    public void deleteAllOfTracksFromPlaylistById(String playlistId) {
        PlaylistTrack[] trackInPlaylist = spotifyApi.getPlaylistsItems(playlistId)
                .build()
                .execute()
                .getItems();

        Optional<PlaylistTrack[]> optionalOfPlaylistTracks = Optional.ofNullable(trackInPlaylist);

        optionalOfPlaylistTracks.ifPresentOrElse(tracks -> {

            JsonArray removeTracks = new JsonArray();
            Arrays.stream(tracks).forEach(track -> {
                JsonObject trackObj = new JsonObject();
                trackObj.addProperty("uri", track.getTrack().getUri());
                removeTracks.add(trackObj);
            });

            try {
                spotifyApi.removeItemsFromPlaylist(playlistId, removeTracks).build().execute();

                List<SpotifyTrackFromPlaylist> tracksToRemove = trackRepository
                        .findAllByUserPlaylistId(playlistId);
                trackRepository.deleteAll(tracksToRemove);
            } catch (Exception e) {
                System.out.println("Something is wrong: " + e.getMessage());
            }
            System.out.println("Successfully removed");
        },
        () -> System.out.println("Playlist is empty.")
        );
    }

}
