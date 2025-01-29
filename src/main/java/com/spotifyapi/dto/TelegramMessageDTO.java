package com.spotifyapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TelegramMessageDTO {

    private String email;
    private List<SpotifyReleaseDTO> releases;

}
