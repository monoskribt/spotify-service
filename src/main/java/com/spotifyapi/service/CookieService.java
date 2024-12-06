package com.spotifyapi.service;

import com.spotifyapi.dto.CookieDTO;
import com.spotifyapi.dto.TokensDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface CookieService {

    void setCookie(HttpServletResponse response, TokensDTO tokens);

    CookieDTO getCookie(HttpServletRequest request);

    boolean isExpired(CookieDTO cookie);
}
