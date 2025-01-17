package com.spotifyapi.controller;

import com.spotifyapi.model.SpotifyArtist;
import com.spotifyapi.service.SpotifyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/spotify")
@AllArgsConstructor
public class SpotifyController {

    private SpotifyService spotifyService;

    @GetMapping("/artist")
    public List<SpotifyArtist> getMyArtist() {
        return spotifyService.getFollowedArtist();
    }

    @GetMapping("/release")
    public List<AlbumSimplified> getReleasesByPeriod(
            @RequestParam (value = "releaseOfDay", required = false) Long releaseOfDay) {
        return spotifyService.getReleases(releaseOfDay);
    }

    @GetMapping("/my-playlists")
    public Set<PlaylistSimplified> getMyPlaylists() {
        return spotifyService.getOfUsersPlaylists();
    }

    @PostMapping("/save-releases")
    public ResponseEntity<String> saveReleasesToPlaylist(@RequestParam ("playlistId") String playlistId,
                                                         @RequestParam ("releaseOfDay") Long releaseOfDay) {
        try {
            String result = spotifyService.saveReleasesToPlaylistById(playlistId, releaseOfDay);
            if("Releases are already in your playlist".equals(result)) {
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is wrong: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-all-from-playlist")
    public ResponseEntity<String> deleteAllItemsFromPlaylistById(@RequestParam("playlistId") String playlistId) {
        try {
            String result = spotifyService.deleteAllOfTracksFromPlaylistById(playlistId);

            if ("Playlist is already empty".equals(result)) {
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is wrong: " + e.getMessage());
        }
    }
}
