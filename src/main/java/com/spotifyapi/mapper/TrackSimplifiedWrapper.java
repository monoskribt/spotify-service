package com.spotifyapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.util.Map;

public class TrackSimplifiedWrapper extends AbstractTrack {

    private final TrackSimplified trackSimplified;

    public TrackSimplifiedWrapper(TrackSimplified trackSimplified) {
        this.trackSimplified = trackSimplified;
    }

    @Override
    public String getId() {
        return trackSimplified.getId();
    }

    @Override
    public String getName() {
        return trackSimplified.getName();
    }

    @Override
    public Map<String, String> getExternalUrls() {
        return trackSimplified.getExternalUrls().getExternalUrls();
    }

    @Override
    public ArtistSimplified[] getArtists() {
        return trackSimplified.getArtists();
    }
}
