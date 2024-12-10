package com.spotifyapi.service.impl;

import com.spotifyapi.constant.ConstantTimeForCookie;
import com.spotifyapi.dto.CookieDTO;
import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CookieServiceImpl implements CookieService {




    @Override
    public void setCookieAccessToken(HttpServletResponse response, TokensDTO tokens) {
        Cookie accessToken = new Cookie("access_token", tokens.getAccessToken());

        accessToken.setMaxAge(ConstantTimeForCookie.TIME_FOR_ACCESS_TOKEN_COOKIE);
        accessToken.setPath("/");

        response.addCookie(accessToken);
    }

    @Override
    public void setCookieRefreshToken(HttpServletResponse response, TokensDTO tokens) {
        Cookie refreshToken = new Cookie("refresh_token", tokens.getRefreshToken());

        refreshToken.setMaxAge(ConstantTimeForCookie.TIME_FOR_REFRESH_TOKEN_COOKIE);
        refreshToken.setPath("/");

        response.addCookie(refreshToken);
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
