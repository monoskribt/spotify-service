package com.spotifyapi.repository;

import com.spotifyapi.model.SpotifyUserPlaylist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PlaylistRepository extends JpaRepository<SpotifyUserPlaylist, String> {

    boolean existsById(String id);

}
