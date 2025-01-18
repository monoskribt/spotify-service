package com.spotifyapi.controller;

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
    public Map<String, String> getInfoAboutUser() {
        Map<String, String> map = new HashMap<>();
        map.put("nickname", userService.getCurrentUsername());
        map.put("status", userService.getSubscribeStatusUsers());
        return map;
    }


    @PutMapping("/subscribe")
    public ResponseEntity<Map<String, String>> manageSubscribeStatus(@RequestParam SubscribeStatus subscribeStatus) {
        try {
            userService.manageSubscribeStatusOfUser(subscribeStatus);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Subscription status updated successfully");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
