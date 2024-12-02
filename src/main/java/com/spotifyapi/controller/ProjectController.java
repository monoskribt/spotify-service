package com.spotifyapi.controller;

import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.service.SpotifyAuth;
import com.spotifyapi.repository.UserRepository;
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

    private final SpotifyAuth spotifyAuth;
    private final UserService userService;

    @GetMapping("/login")
    public void spotifyLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect(spotifyAuth.authorize());
    }

    @GetMapping("/profile")
    public String getProfile(@RequestParam String code) {
        TokensDTO tokens = spotifyAuth.getAuthorizationTokens(code);
        userService.saveUserData(tokens);
        return "User profile saved successfully!";
    }
}
