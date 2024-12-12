package com.spotifyapi.filter;

import com.spotifyapi.dto.CookieDTO;
import com.spotifyapi.dto.TokensDTO;
import com.spotifyapi.service.CookieService;
import com.spotifyapi.service.SpotifyAuth;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@AllArgsConstructor
public class TokenValidation extends OncePerRequestFilter {

    private final CookieService cookieService;
    private final SpotifyAuth spotifyAuth;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        CookieDTO cookie = cookieService.getCookie(request);

        if (cookie.getAccessTokenCookie() == null) {
            if (cookie.getRefreshTokenCookie() != null) {
                try {
                    TokensDTO tokens = spotifyAuth.getNewAccessToken(cookie.getRefreshTokenCookie().getValue());
                    cookieService.setCookieAccessToken(response, tokens);
                } catch (Exception e) {
                    response.sendRedirect("/api/login");
                    return;
                }
            } else {
                response.sendRedirect("/api/login");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().equals("/api/login") || request.getRequestURI().equals("/api/profile");
    }
}