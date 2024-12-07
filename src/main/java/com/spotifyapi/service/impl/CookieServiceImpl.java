package com.spotifyapi.service.impl;

import com.spotifyapi.dto.CookieDTO;
import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.service.CookieService;
import com.spotifyapi.service.SpotifyAuth;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CookieServiceImpl implements CookieService {

    private final SpotifyAuth spotifyAuth;

    @Override
    public void setCookie(HttpServletResponse response, TokensDTO tokens) {
        Cookie cookieAccessToken = new Cookie("access_token", tokens.getAccessToken());
        Cookie cookieRefreshToken = new Cookie("refresh_token", tokens.getRefreshToken());

        cookieAccessToken.setMaxAge(60 * 60);
        cookieRefreshToken.setMaxAge(7 * 24 * 60 * 60);

        cookieAccessToken.setPath("/");
        cookieRefreshToken.setPath("/");

        response.addCookie(cookieAccessToken);
        response.addCookie(cookieRefreshToken);
    }

    @Override
    public CookieDTO getCookie(HttpServletRequest request) {
        Cookie[] cookies = Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]);

        Cookie accessToken = null;
        Cookie refreshToken = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("access_token")) {
                accessToken = cookie;
            } else if (cookie.getName().equals("refresh_token")) {
                refreshToken = cookie;
            }

            if (accessToken != null && refreshToken != null) {
                break;
            }
        }

        return new CookieDTO(accessToken, refreshToken);

    }


}
