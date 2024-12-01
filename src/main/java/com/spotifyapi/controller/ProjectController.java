package com.spotifyapi.controller;

import com.spotifyapi.service.SpotifyAuth;
import com.spotifyapi.service.UserRepository;
import com.spotifyapi.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final SpotifyAuth spotifyAuth;
    private final UserServiceImpl userService;

    @GetMapping("/login")
    public void spotifyLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect(spotifyAuth.authorize());
    }

    @GetMapping("/profile")
    public String getProfile(@RequestParam String code) {
        List<String> getTokens = spotifyAuth.getAuthorizationTokens(code);
        userService.saveUserData(getTokens);
        return "User profile saved successfully!";
    }
}
