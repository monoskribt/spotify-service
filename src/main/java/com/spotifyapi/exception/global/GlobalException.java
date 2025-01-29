package com.spotifyapi.exception.global;

import com.spotifyapi.exception.SpotifyAuthException;
import com.spotifyapi.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleBadRequestException(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Something went wrong: " + exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(UserNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Something went wrong: " + exception.getMessage());
    }

    @ExceptionHandler(SpotifyAuthException.class)
    public ResponseEntity<String> handleUnauthorizedException(SpotifyAuthException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid or failed token: " + exception.getMessage());
    }
}
