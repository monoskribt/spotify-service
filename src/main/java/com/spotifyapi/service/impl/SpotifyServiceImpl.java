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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.spotifyapi.constant.ConstantDayForReleases.THIRTY_DAYS;
import static com.spotifyapi.constant.ConstantResponses.*;

@Service
@AllArgsConstructor
public class SpotifyServiceImpl implements SpotifyService {

    private final SpotifyApi spotifyApi;
    private final PlaylistRepository playlistRepository;
    private final SpotifyTrackService spotifyTrackService;
    private final TrackRepository trackRepository;

    private static final Logger logger = LoggerFactory.getLogger(SpotifyServiceImpl.class);

    @SneakyThrows
    @Override
    public Set<PlaylistSimplified> getOfUsersPlaylists(String authorizationHeader) {
        return Arrays.stream(spotifyApi.getListOfCurrentUsersPlaylists()
                .build()
                .execute().getItems()).collect(Collectors.toSet());
    }

    @Override
    @SneakyThrows
    public List<SpotifyArtist> getFollowedArtist(String authorizationHeader) {

        
        List<SpotifyArtist> followedArtists = new ArrayList<>();
        int limit = 50;
        String cursor = "0";

        while (cursor != null) {
            var responseFollowerArtists = spotifyApi.getUsersFollowedArtists(ModelObjectType.ARTIST)
                    .limit(limit)
                    .after(cursor)
                    .build()
                    .execute();

            if(responseFollowerArtists != null) {
                followedArtists.addAll(Arrays.stream(responseFollowerArtists.getItems())
                        .map(artist -> new SpotifyArtist(artist.getId(), artist.getName()))
                        .toList());
                cursor = responseFollowerArtists.getCursors()[0].getAfter();
            } else {
                break;
            }
        }

        return followedArtists;
    }

    @Override
    @SneakyThrows
    public List<AlbumSimplified> getReleases(String authorizationHeader) {
        return getReleases(authorizationHeader, THIRTY_DAYS);
    }

    @Override
    @SneakyThrows
    public List<AlbumSimplified> getReleases(String authorizationHeader, Long releaseOfDay) {
        List<SpotifyArtist> artists = getFollowedArtist(authorizationHeader);

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
    @Transactional
    public String saveReleasesToPlaylistById(String authorizationHeader, String playlistId, Long releaseOfDay) {
        List<AlbumSimplified> releases = getReleases(authorizationHeader, releaseOfDay);
        List<String> trackUrl = new ArrayList<>();
        List<SpotifyTrackFromPlaylist> saveTrackToDB = new ArrayList<>();

        Optional<SpotifyUserPlaylist> playlist = playlistRepository.findById(playlistId);

        Set<String> existingTrackIds = spotifyTrackService
                .getExistingTrackIds(playlist.get().getId());

        boolean checkToAddTrack = false;

        for (AlbumSimplified album : releases) {
            int offset = 0;
            boolean hasMore = true;

            while (hasMore) {
                var tracksPage = spotifyApi.getAlbumsTracks(album.getId())
                        .limit(50)
                        .offset(offset)
                        .build()
                        .execute();

                for (TrackSimplified track : tracksPage.getItems()) {
                    if(track != null && !existingTrackIds.contains(track.getId())) {
                        trackUrl.add(track.getUri());

                        SpotifyTrackFromPlaylist trackEntity = spotifyTrackService
                                .convertTrackToTrackDBEntity(new TrackSimplifiedWrapper(track), playlist.get());

                        saveTrackToDB.add(trackEntity);
                        checkToAddTrack = true;
                    }
                }

                hasMore = tracksPage.getNext() != null;
                offset += 50;
            }

        }

        if (checkToAddTrack) {
            trackRepository.saveAll(saveTrackToDB);

            for (int i = 0; i < trackUrl.size(); i += 50) {
                List<String> trackUrlPart = trackUrl.subList(i, Math.min(i + 50, trackUrl.size()));

                spotifyApi.addItemsToPlaylist(playlistId, trackUrlPart.toArray(new String[0]))
                        .build()
                        .execute();
            }

            return SUCCESSFULLY_ADDED;
        } else {
            return RELEASE_IS_ALREADY_EXIST;
        }
    }

    @SneakyThrows
    @Override
    @Transactional
    public String deleteAllOfTracksFromPlaylistById(String authorizationHeader, String playlistId) {
        List<PlaylistTrack> allTracks = new ArrayList<>();

        int limit = 50;
        int offset = 0;
        boolean hasMore = true;

        while (hasMore) {
            PlaylistTrack[] trackInPlaylist = spotifyApi.getPlaylistsItems(playlistId)
                    .offset(offset)
                    .limit(limit)
                    .build()
                    .execute()
                    .getItems();

            if (trackInPlaylist != null) {
                allTracks.addAll(Arrays.asList(trackInPlaylist));
            } else {
                logger.info("Playlist is empty.");
                break;
            }

            hasMore = trackInPlaylist.length == limit;
            offset += limit;
        }

        if (allTracks.isEmpty()) {
            logger.info("Playlist is already empty");
            return PLAYLIST_IS_EMPTY;
        }

        JsonArray removeTracks = new JsonArray();

        for(PlaylistTrack track : allTracks) {
            JsonObject trackObj = new JsonObject();
            trackObj.addProperty("uri", track.getTrack().getUri());
            removeTracks.add(trackObj);
        }

        try {
            for(int i = 0; i < removeTracks.size(); i+= 50) {
                JsonArray pathArrayToRemove = new JsonArray();

                for(int j = i; j < Math.min(i + 50, removeTracks.size()); j++) {
                    pathArrayToRemove.add(removeTracks.get(j));
                }

                spotifyApi.removeItemsFromPlaylist(playlistId, pathArrayToRemove)
                        .build()
                        .execute();
            }

            List<SpotifyTrackFromPlaylist> tracksToRemove = trackRepository.findAllByUserPlaylistId(playlistId);
            trackRepository.deleteAll(tracksToRemove);

            return SUCCESSFULLY_REMOVED;
        } catch (Exception e) {
            logger.error("Error while removing tracks from playlist: {}", e.getMessage());
            return "Something went wrong: " + e.getMessage();
        }
    }
}
