package com.spotifyapi.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "user_spotify")
public class User {

    @Id
    private String id;

    @Column(name = "username")
    private String username;

    @Column(unique = true, name = "email")
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<SpotifyUserPlaylist> userPlaylists;

}
