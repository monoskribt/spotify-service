package com.spotifyapi.config;


import com.spotifyapi.props.SpotifyProps;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import se.michaelthelin.spotify.SpotifyApi;

import java.net.URI;

@Configuration
@AllArgsConstructor
public class ProjectConfig {

    private final SpotifyProps spotifyProps;

    @Bean
    public SpotifyApi spotifyApi() {
        return new SpotifyApi.Builder()
                .setClientId(spotifyProps.getClientId())
                .setClientSecret(spotifyProps.getClientSecret())
                .setRedirectUri(URI.create(spotifyProps.getRedirectUrl()))
                .build();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/get-user-data").authenticated()
                )
                .oauth2Login(Customizer.withDefaults());

        return http.build();
    }



    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return clientId -> ClientRegistration.withRegistrationId("spotify")
                .clientId(clientId)
                .clientSecret(spotifyProps.getClientSecret())
                .redirectUri(spotifyProps.getRedirectUrl())
                .authorizationUri("https://accounts.spotify.com/authorize")
                .tokenUri("https://accounts.spotify.com/api/token")
                .userInfoUri("https://api.spotify.com/v1/me")
                .scope("user-read-private", "user-read-email")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
    }
}
