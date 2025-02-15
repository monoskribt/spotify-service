package com.spotifyapi.service.impl;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.spotifyapi.dto.spotify_entity.SpotifyArtistDTO;
import com.spotifyapi.dto.spotify_entity.SpotifyPlaylistsDTO;
import com.spotifyapi.dto.spotify_entity.SpotifyReleaseDTO;
import com.spotifyapi.exception.PlaylistNotFoundException;
import com.spotifyapi.mapper.TrackSimplifiedWrapper;
import com.spotifyapi.model.ProgressArtistsUpdate;
import com.spotifyapi.model.SpotifyArtist;
import com.spotifyapi.model.SpotifyTrackFromPlaylist;
import com.spotifyapi.model.SpotifyUserPlaylist;
import com.spotifyapi.repository.PlaylistRepository;
import com.spotifyapi.repository.TrackRepository;
import com.spotifyapi.service.SpotifyService;
import com.spotifyapi.service.SpotifyTrackService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.ModelObjectType;
import se.michaelthelin.spotify.model_objects.specification.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.spotifyapi.constant.ConstantDayForReleases.THIRTY_DAYS;
import static com.spotifyapi.constant.ConstantResponses.*;

@Service
@AllArgsConstructor
@EnableAsync
@Slf4j
public class SpotifyServiceImpl implements SpotifyService {

    private final SpotifyApi spotifyApi;
    private final PlaylistRepository playlistRepository;
    private final SpotifyTrackService spotifyTrackService;
    private final TrackRepository trackRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(SpotifyServiceImpl.class);

    @Override
    @SneakyThrows
    @Cacheable(value = "artists", key = "#authorizationHeader")
    public <T> List<T> getFollowedArtist(String authorizationHeader, Class<T> returnTypeOfClass) {
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

        log.info("working is method 'getFollowedArtist', but not cache");
        return followedArtists.stream()
                .map(artis -> createNewInstanceOfArtist(returnTypeOfClass, artis))
                .collect(Collectors.toList());
    }


    private <T> T createNewInstanceOfArtist(Class<T> returnTypeOf, SpotifyArtist artist) {
        if(returnTypeOf.equals(SpotifyArtist.class)) {
            return returnTypeOf.cast(artist);
        } else if(returnTypeOf.equals(SpotifyArtistDTO.class)) {
            return returnTypeOf.cast(new SpotifyArtistDTO(artist.getName()));
        }

        throw new IllegalArgumentException("Error type of class: " + returnTypeOf.getName());
    }


    @Override
    @SneakyThrows
    @Cacheable(value = "playlists", key = "#authorizationHeader")
    public Set<SpotifyPlaylistsDTO> getOfUsersPlaylists(String authorizationHeader) {
        var listOfPlaylist = Arrays.stream(spotifyApi.getListOfCurrentUsersPlaylists()
                .build()
                .execute().getItems()).collect(Collectors.toSet());

        log.info("working is method 'getOfUsersPlaylists', but not cache");
        return listOfPlaylist.stream()
                .map(playlist -> new SpotifyPlaylistsDTO(playlist.getId(), playlist.getName()))
                .collect(Collectors.toSet());
    }


    @Override
    @SneakyThrows
    public List<AlbumSimplified> getReleases(String authorizationHeader) {
        return getReleases(authorizationHeader, THIRTY_DAYS, AlbumSimplified.class);
    }

    @Override
    @SneakyThrows
    @Cacheable(value = "releases", key = "#authorizationHeader + '_' + #releaseOfDay")
    public <T> List<T> getReleases(String authorizationHeader, Long releaseOfDay,
                                   Class<T> returnTypeOfClass) {
        List<SpotifyArtist> artists = getFollowedArtist(authorizationHeader, SpotifyArtist.class);
        List<AlbumSimplified> listOfAlbums = Collections.synchronizedList(new ArrayList<>());

        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        int totalArtists = artists.size();
        AtomicInteger processedArtists = new AtomicInteger(0);

        List<CompletableFuture<Void>> futures = artists.stream()
                .map(artist -> CompletableFuture.runAsync(() -> {
                    try {
                        var items = spotifyApi.getArtistsAlbums(artist.getId())
                                .build()
                                .execute();

                        if (items == null || items.getItems().length == 0) {
                            log.info("No albums for artist: {}", artist.getName());
                            return;
                        }

                        var albums = Arrays.stream(items.getItems())
                                .filter(album -> {
                                    try {
                                        if (album.getReleaseDate() == null || album.getReleaseDate().isEmpty()) {
                                            return false;
                                        }

                                        LocalDate releaseDate;
                                        String releaseDateString = album.getReleaseDate();

                                        if (releaseDateString.length() == 4) {
                                            releaseDate = LocalDate.parse(releaseDateString + "-01-01", yearFormatter);
                                        } else {
                                            releaseDate = LocalDate.parse(releaseDateString, yearMonthDayFormatter);
                                        }

                                        return releaseDate.isAfter(LocalDate.now().minusDays(releaseOfDay));
                                    } catch (DateTimeParseException e) {
                                        log.error("Error parsing release date for album: {}. Error: {}", album.getName(), e.getMessage());
                                        return false;
                                    }
                                })
                                .toList();

                        listOfAlbums.addAll(albums);

                        int progress = processedArtists.incrementAndGet();
                        int remaining = totalArtists - progress;
                        log.info("Processed artist: {}, remaining: {}", artist.getName(), remaining);
                        messagingTemplate.convertAndSend("/topic/progress", new ProgressArtistsUpdate(progress, totalArtists));
                    } catch (Exception e) {
                        log.error("Error processing artist {}: {}", artist.getName(), e.getMessage());
                    }
                }, Executors.newFixedThreadPool(10)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        log.info("Size of listOfAlbums: {}", listOfAlbums.size());
        log.info("working is method 'getReleases', but not cache");
        return listOfAlbums.stream()
                .map(release -> createNewInstanceOfReleases(returnTypeOfClass, release))
                .collect(Collectors.toList());
    }


    private <T> T createNewInstanceOfReleases(Class<T> returnTypeOf, AlbumSimplified album) {
        if(returnTypeOf.equals(AlbumSimplified.class)) {
            return returnTypeOf.cast(album);
        } else if(returnTypeOf.equals(SpotifyReleaseDTO.class)) {
            return returnTypeOf.cast(new SpotifyReleaseDTO(album.getId(), album.getName()));
        }

        throw new IllegalArgumentException("Error type of class: " + returnTypeOf.getName());
    }

    @SneakyThrows
    @Override
    @Transactional
    public String saveReleasesToPlaylistById(String authorizationHeader, String playlistId, Long releaseOfDay) {
        List<AlbumSimplified> releases =
                getReleases(authorizationHeader, releaseOfDay, AlbumSimplified.class);
        List<String> trackUrl = new ArrayList<>();
        List<SpotifyTrackFromPlaylist> saveTrackToDB = new ArrayList<>();

        SpotifyUserPlaylist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new PlaylistNotFoundException("Playlist not found"));

        Set<String> existingTrackIds = spotifyTrackService
                .getExistingTrackIds(playlist.getId());

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
                                .convertTrackToTrackDBEntity(new TrackSimplifiedWrapper(track), playlist);

                        saveTrackToDB.add(trackEntity);
                        checkToAddTrack = true;
                    }
                }

                hasMore = tracksPage.getNext() != null;
                offset += 50;
            }

        }

        log.info("List with releases 'Save Track To DB' is: {}", saveTrackToDB.size());
        if(saveTrackToDB.isEmpty()) {
            return NOTHING_SAVE_TO_DB;
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