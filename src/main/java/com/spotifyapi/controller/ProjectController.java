package com.spotifyapi.controller;

import com.spotifyapi.service.SpotifyAuth;
import com.spotifyapi.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ProjectController {

    private SpotifyAuth spotifyAuth;
    private UserService userService;

    @GetMapping("/login")
    public void spotifyLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect(spotifyAuth.authorize());
    }

    @GetMapping("/profile")
    public String getProfile(@RequestParam String code) {
        spotifyAuth.setAccessToken(code);
        return userService.getUserData();
    }
}
