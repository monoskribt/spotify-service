package com.spotifyapi.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class TokensDTO {

    private String accessToken;
    private String refreshToken;

}
