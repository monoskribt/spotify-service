package com.spotifyapi.repository;

import com.spotifyapi.model.SpotifyTrackFromPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<SpotifyTrackFromPlaylist, String> {

    boolean existsByIdAndUserPlaylistId(String trackId, String playlistId); //todo delete

    List<SpotifyTrackFromPlaylist> findAllByUserPlaylistId(String playlistId);

}
