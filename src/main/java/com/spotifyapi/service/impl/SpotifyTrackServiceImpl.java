package com.spotifyapi.service.impl;

import com.spotifyapi.model.SpotifyTrackFromPlaylist;
import com.spotifyapi.model.SpotifyUserPlaylist;
import com.spotifyapi.repository.PlaylistRepository;
import com.spotifyapi.repository.TrackRepository;
import com.spotifyapi.service.SpotifyTrackService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.TrackSimplified;

import java.util.Optional;

@Service
@AllArgsConstructor
public class SpotifyTrackServiceImpl implements SpotifyTrackService {

    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;

    @Override
    public void saveTracks(Track track, SpotifyUserPlaylist playlist) {
        SpotifyTrackFromPlaylist trackFromPlaylist = new SpotifyTrackFromPlaylist();
        Optional<SpotifyUserPlaylist> spotifyUserPlaylist = playlistRepository.findById(playlist.getId());


        if(!isAlreadyExist(track.getId(), spotifyUserPlaylist.get().getId())) {
            trackFromPlaylist.setId(track.getId());
            trackFromPlaylist.setName(track.getName());
            trackFromPlaylist.setExternalUrl(track.getExternalUrls().get("spotify"));
            trackFromPlaylist.setArtistName(track.getArtists()[0].getName());

            trackFromPlaylist.setUserPlaylist(spotifyUserPlaylist.get());

            trackRepository.save(trackFromPlaylist);
        }
    }

    @Override
    public boolean isAlreadyExist(String trackId, String playlistId) {
        return trackRepository.existsByIdAndUserPlaylistId(trackId, playlistId);
    }

    @Override
    public Track convertToTrackFormat(TrackSimplified trackSimplified) {
        return new Track.Builder()
                .setId(trackSimplified.getId())
                .setName(trackSimplified.getName())
                .setExternalUrls(trackSimplified.getExternalUrls())
                .setArtists(trackSimplified.getArtists())
                .build();
    }


}
