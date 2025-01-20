package com.spotifyapi.model;


import com.spotifyapi.enums.SubscribeStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "subscribe_status", columnDefinition = "varchar(255) default 'UNSUBSCRIBE'")
    private SubscribeStatus subscribeStatus = SubscribeStatus.UNSUBSCRIBE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpotifyUserPlaylist> userPlaylists;

}
