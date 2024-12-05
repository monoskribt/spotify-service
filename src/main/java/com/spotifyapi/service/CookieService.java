package com.spotifyapi.service;

import com.spotifyapi.dto.TokensDTO;
import jakarta.servlet.http.HttpServletResponse;


public interface CookieService {

    void setCookie(HttpServletResponse response, TokensDTO tokens);

}
