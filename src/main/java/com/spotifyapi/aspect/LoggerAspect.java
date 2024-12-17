package com.spotifyapi.aspect;

import com.spotifyapi.model.Logger;
import com.spotifyapi.repository.LoggerRepository;
import com.spotifyapi.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
@AllArgsConstructor
@Slf4j
public class LoggerAspect {

    private final LoggerRepository loggerRepository;
    private final UserService userService;

    @AfterReturning(pointcut = "execution(* com.spotifyapi.controller.SpotifyController.*(..))", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String listOfParam = Arrays.toString(joinPoint.getArgs());
        String returnValue = (result != null) ? result.toString() : "null";

        Logger logger = new Logger();
        logger.setUsername(userService.getCurrentUsername());
        logger.setUserId(userService.getCurrentId());
        logger.setMethodName(methodName);
        logger.setParameters(listOfParam);
        logger.setDateTime(LocalDateTime.now());
        logger.setMessage("Successfully. Returned: " + returnValue);
        logger.setStatus("SUCCESS");

        loggerRepository.save(logger);
    }

    @AfterThrowing(pointcut = "execution(* com.spotifyapi.controller.SpotifyController.*(..))", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String listOfParam = Arrays.toString(joinPoint.getArgs());
        String exceptionMessage = exception.toString();

        Logger logger = new Logger();
        logger.setUsername(userService.getCurrentUsername());
        logger.setUserId(userService.getCurrentId());
        logger.setMethodName(methodName);
        logger.setParameters(listOfParam);
        logger.setDateTime(LocalDateTime.now());
        logger.setMessage("Failed. Exception: " + exceptionMessage);
        logger.setStatus("FAILED");

        loggerRepository.save(logger);
    }
}
