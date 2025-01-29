package com.spotifyapi.controller;

import com.spotifyapi.dto.UserInfoDTO;
import com.spotifyapi.enums.SubscribeStatus;
import com.spotifyapi.exception.UserNotFoundException;
import com.spotifyapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/info")
    public UserInfoDTO getInfoAboutUser() {
        return new UserInfoDTO(
                userService.getCurrentUsername(),
                userService.getSubscribeStatusUsers());
    }


    @PutMapping("/subscribe")
    public ResponseEntity<String> manageSubscribeStatus(@RequestParam SubscribeStatus subscribeStatus) {
        userService.manageSubscribeStatusOfUser(subscribeStatus);
        return ResponseEntity.ok("Subscribe status updated successfully");
    }
}
