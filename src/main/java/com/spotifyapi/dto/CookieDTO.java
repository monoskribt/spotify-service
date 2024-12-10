package com.spotifyapi.dto;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CookieDTO {

    private Cookie accessTokenCookie;
    private Cookie refreshTokenCookie;

}
