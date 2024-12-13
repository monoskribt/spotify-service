package com.spotifyapi.controller;

import com.spotifyapi.model.SpotifyArtist;
import com.spotifyapi.service.SpotifyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
