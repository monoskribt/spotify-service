package com.spotifyapi.config;


import com.spotifyapi.props.SpotifyProps;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import se.michaelthelin.spotify.SpotifyApi;

import java.net.URI;

@Configuration
@AllArgsConstructor
public class ProjectConfig {

    private final SpotifyProps spotifyProps;

    @Bean
    public SpotifyApi spotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId(spotifyProps.clientId())
                .setClientSecret(spotifyProps.clientSecret())
                .setRedirectUri(URI.create(spotifyProps.redirectUrl()))
                .build();
    }


}