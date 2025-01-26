package com.spotifyapi.controller;

import com.spotifyapi.model.SpotifyArtist;
import com.spotifyapi.service.SpotifyService;
import com.spotifyapi.service.TokenService;
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

    @GetMapping("/artists")
    public List<SpotifyArtist> getMyArtist(@RequestHeader(value = "Authorization") String authorizationHeader) {

        return spotifyService.getFollowedArtist(authorizationHeader);
    }

    @GetMapping("/releases")
    public List<AlbumSimplified> getReleasesByPeriod(
            @RequestParam (value = "releaseOfDay", required = false) Long releaseOfDay,
            @RequestHeader(value = "Authorization") String authorizationHeader) {
        return spotifyService.getReleases(authorizationHeader, releaseOfDay);
    }

    @GetMapping("/playlists")
    public Set<PlaylistSimplified> getMyPlaylists(
            @RequestHeader(value = "Authorization") String authorizationHeader) {
        return spotifyService.getOfUsersPlaylists(authorizationHeader);
    }

    @PostMapping("/playlists/{playlistId}/releases")
    public ResponseEntity<String> saveReleasesToPlaylist(@PathVariable ("playlistId") String playlistId,
                                                         @RequestParam ("releaseOfDay") Long releaseOfDay,
                                                         @RequestHeader(value = "Authorization") String authorizationHeader) {
        try {
            String result = spotifyService.saveReleasesToPlaylistById(authorizationHeader, playlistId, releaseOfDay);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is wrong: " + e.getMessage());
        }
    }

    @DeleteMapping("/playlists/{playlistId}/items")
    public ResponseEntity<String> deleteAllItemsFromPlaylistById(@PathVariable("playlistId") String playlistId,
                                                                 @RequestHeader(value = "Authorization") String authorizationHeader) {
        try {
            String result = spotifyService.deleteAllOfTracksFromPlaylistById(authorizationHeader, playlistId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something is wrong: " + e.getMessage());
        }
    }
}
