package com.spotifyapi.service.impl;

import com.spotifyapi.model.SpotifyRelease;
import com.spotifyapi.model.User;
import com.spotifyapi.repository.ReleaseRepository;
import com.spotifyapi.service.RabbitMQService;
import com.spotifyapi.service.SpotifyReleaseService;
import com.spotifyapi.service.SpotifyService;
import com.spotifyapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpotifyReleaseServiceImpl implements SpotifyReleaseService {

    private final ReleaseRepository releaseRepository;
    private final SpotifyService spotifyService;
    private final RabbitMQService rabbitMQService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(SpotifyReleaseServiceImpl.class);

    @Override
    public void save(Set<SpotifyRelease> releaseList) {
        releaseRepository.saveAll(releaseList);
    }


    private List<SpotifyRelease> getReleasesByUserId(String id) {
        return releaseRepository.findByUserId(id);
    }

    @Override
    public void checkReleasesForAllUsers() {
        Set<User> userList = userService.getAllUsersWithSubscribeStatus();

        Map<User, Set<SpotifyRelease>> userReleases = userList.stream()
                .collect(Collectors.toMap(
                        user -> user,
                        this::checkReleasesForUser
                ));

        userReleases.entrySet().stream()
                .filter(release -> !release.getValue().isEmpty())
                .forEach(notification -> rabbitMQService
                        .sendMessageToTelegram(notification.getKey(), notification.getValue()));
    }

    private Set<SpotifyRelease> checkReleasesForUser(User user) {
        List<AlbumSimplified> albumList = spotifyService.getReleases();

        List<String> alreadyContainsReleasesId = getReleasesByUserId(user.getId())
                .stream()
                .map(SpotifyRelease::getId)
                .toList();
        logger.info(String.valueOf(alreadyContainsReleasesId));

        Set<SpotifyRelease> checkListRelease = albumList.stream()
                .filter(release -> !alreadyContainsReleasesId.contains(release.getId()))
                .map(release -> {
                    SpotifyRelease spotifyRelease = new SpotifyRelease();
                    spotifyRelease.setId(release.getId());
                    spotifyRelease.setName(release.getName());
                    spotifyRelease.setLocalDate(LocalDate.now());
                    spotifyRelease.setUser(user);
                    return spotifyRelease;
                })
                .collect(Collectors.toSet());

        save(checkListRelease);
        return checkListRelease;
    }
}
