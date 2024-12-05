package com.spotifyapi.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user_spotify")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(unique = true, name = "email")
    private String email;

    @Column(name = "spotify_user_id")
    private String spotifyUserId;

    @Column(name = "access_code")
    private String accessCode;

}
