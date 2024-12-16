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

@RestController
@RequestMapping("/api/spotify")
@AllArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;

    @GetMapping("/artist")
    public List<SpotifyArtist> getMyArtist() {
        return spotifyService.getFollowedArtist();
    }

    @GetMapping("/release")
    public List<AlbumSimplified> getReleasesByLastSixMonth() {
        return spotifyService.getReleases();
    }

    @GetMapping("/my-playlists")
    private List<PlaylistSimplified> getMyPlaylists() {
        return spotifyService.getOfUserPlaylists();
    }

    @PostMapping("/save-releases")
    public ResponseEntity<String> saveReleasesToPlaylist(@RequestParam ("playlistId") String playlistId) {
        try {
            spotifyService.saveReleasesToPlaylistById(playlistId);
            return ResponseEntity.ok("Successfully added");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is wrong: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete-all-from-playlist")
    public ResponseEntity<String> deleteAllItemsFromPlaylistById(@RequestParam ("playlistId") String playlistId) {
        try {
            spotifyService.deleteAllOfTracksFromPlaylistById(playlistId);
            return ResponseEntity.ok("Successfully removed");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is wrong: " + e.getMessage());
        }
    }

}
