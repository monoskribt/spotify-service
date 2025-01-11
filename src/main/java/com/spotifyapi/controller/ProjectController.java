package com.spotifyapi.controller;

import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.props.CorsConfigurationProps;
import com.spotifyapi.service.CookieService;
import com.spotifyapi.service.SpotifyAuth;
import com.spotifyapi.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProjectController {

    private final SpotifyAuth spotifyAuth;
    private final UserService userService;
    private final CookieService cookieService;
    private final CorsConfigurationProps corsProps;

    @GetMapping("/login")
    public String spotifyLogin(HttpServletResponse response) throws IOException {
        // response.sendRedirect(spotifyAuth.authorize());
        return spotifyAuth.authorize();
    }

    @GetMapping("/profile")
    public void getProfile(@RequestParam String code, HttpServletResponse response) throws IOException {
        TokensDTO tokens = spotifyAuth.getAuthorizationTokens(code);

        cookieService.setCookieAccessToken(response, tokens);
        cookieService.setCookieRefreshToken(response, tokens);

        if(!userService.isAlreadyExist()) {
            userService.saveUserOfData(tokens);

        }

        response.sendRedirect(corsProps.getAllowedOrigins());
    }


    @GetMapping("/info")
    public String getInfo() {
        return "Welcome to your info";
    }
}
