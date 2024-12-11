package com.spotifyapi.controller;

import com.spotifyapi.model.SpotifyArtist;
import com.spotifyapi.service.SpotifyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
